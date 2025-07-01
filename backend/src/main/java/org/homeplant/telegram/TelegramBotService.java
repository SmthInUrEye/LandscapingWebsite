package org.homeplant.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.homeplant.model.ConsultationTask;
import org.homeplant.model.Feedback;
import org.homeplant.repository.ConsultationTaskRepository;
import org.homeplant.repository.FeedbackRepository;
import org.homeplant.service.ConsultationTaskService;
import org.homeplant.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class TelegramBotService implements UpdatesListener {

    @Value("${telegram.admin-chat-id}")
    private Long adminChatId; // Единый chatId админа

    private final TelegramBot telegramBot;
    private final ConsultationTaskService consultationTaskService;
    private final ConsultationTaskRepository consultationTaskRepository;
    private final FeedbackService feedbackService;
    private final FeedbackRepository feedbackRepository;

    @Autowired
    public TelegramBotService(TelegramBot telegramBot,
                              ConsultationTaskService consultationTaskService,
                              ConsultationTaskRepository consultationTaskRepository,
                              FeedbackService feedbackService,
                              FeedbackRepository feedbackRepository) {
        this.telegramBot = telegramBot;
        this.consultationTaskService = consultationTaskService;
        this.consultationTaskRepository = consultationTaskRepository;
        this.feedbackService = feedbackService;
        this.feedbackRepository = feedbackRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);

    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            try {
                processUpdate(update);
            } catch (Exception e) {
                sendMessage("❌ Ошибка обработки запроса: " + e.getMessage());
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processUpdate(Update update) {
        if (update.message() != null && update.message().chat().id().equals(adminChatId)) {
            handleCommand(update.message());
        } else if (update.callbackQuery() != null && update.callbackQuery().message().chat().id().equals(adminChatId)) {
            handleCallback(update.callbackQuery());
        }
    }

    private void handleCommand(Message message) {
        String text = message.text().trim();

        switch (text) {
            case "/start":
            case "🆘 Помощь":
                sendWelcomeMessage();
                break;
            case "/tasks":
            case "📋 Последние заявки":
                sendRecentConsultations();
                break;
            case "/feedbacks":
            case "📝 Последние отзывы":
                sendRecentFeedbacks();
                break;
            default:
                sendMessage("Неизвестная команда", getMainMenuKeyboard());
        }
    }

    // Уведомления о новых данных
    @EventListener
    public void handleNewConsultationTask(ConsultationTask task) {
        String message = "✉️ *Новая заявка!*\n" + formatConsultation(task);
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
         new InlineKeyboardButton("❌ Удалить").callbackData("delete_consult_" + task.getId())
        );
        sendMessage(message, keyboard);
    }

    @EventListener
    public void handleNewFeedback(Feedback feedback) {
        String message = "✉️ *Новый отзыв!*\n" + formatFeedback(feedback);
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
         new InlineKeyboardButton("❌ Удалить").callbackData("delete_feedback_" + feedback.getId())
        );
        sendMessage(message, keyboard);
    }

    // Основные методы работы с ботом
    private void sendWelcomeMessage() {
        String text = """
        🌿 Бот управления заявками HomePlant
        
        Доступные команды:
        /tasks - Последние заявки
        /feedbacks - Последние отзывы
        """;
        sendMessage(text, getMainMenuKeyboard());
    }

    private void sendRecentConsultations() {
        List<ConsultationTask> tasks = consultationTaskRepository.findAll();

        if (tasks.isEmpty()) {
            sendMessage("Нет активных заявок", getMainMenuKeyboard());
            return;
        }

        tasks.forEach(task -> {
            String message = formatConsultation(task);
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
             new InlineKeyboardButton("❌ Удалить").callbackData("delete_consult_" + task.getId())
            );
            sendMessage(message, keyboard);
        });
    }

    private void sendRecentFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findAll();

        if (feedbacks.isEmpty()) {
            sendMessage("Нет отзывов", getMainMenuKeyboard());
            return;
        }

        feedbacks.forEach(feedback -> {
            String message = formatFeedback(feedback);
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
             new InlineKeyboardButton("❌ Удалить").callbackData("delete_feedback_" + feedback.getId())
            );
            sendMessage(message, keyboard);
        });
    }

    // Обработка callback-ов
    private void handleCallback(CallbackQuery callback) {
        String data = callback.data();

        try {
            if (data.startsWith("delete_consult_")) {
                UUID id = UUID.fromString(data.substring(15));
                consultationTaskService.deleteTask(id);
                sendMessage("✅ Заявка удалена", getMainMenuKeyboard());
            } else if (data.startsWith("delete_feedback_")) {
                UUID id = UUID.fromString(data.substring(16));
                feedbackService.deleteFeedback(id);
                sendMessage("✅ Отзыв удалён", getMainMenuKeyboard());
            }

            telegramBot.execute(new DeleteMessage(adminChatId, callback.message().messageId()));
        } catch (Exception e) {
            sendMessage("❌ Ошибка: " + e.getMessage());
        }
    }

    // Вспомогательные методы
    private ReplyKeyboardMarkup getMainMenuKeyboard() {
        return new ReplyKeyboardMarkup(
         new String[]{"📋 Последние заявки", "📝 Последние отзывы"},
         new String[]{"🆘 Помощь"}
        ).resizeKeyboard(true);
    }

    private void sendMessage(String text) {
        telegramBot.execute(new SendMessage(adminChatId, text));
    }

    private void sendMessage(String text, InlineKeyboardMarkup keyboard) {
        telegramBot.execute(new SendMessage(adminChatId, text).replyMarkup(keyboard));
    }

    private void sendMessage(String text, ReplyKeyboardMarkup keyboard) {
        telegramBot.execute(new SendMessage(adminChatId, text).replyMarkup(keyboard));
    }

    private String formatConsultation(ConsultationTask task) {
        return String.format(
         """
         📅 Дата: %s
         👤 Имя: %s
         📞 Телефон: %s
         """,
         task.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
         task.getUserName(),
         task.getUserMobileNumber());
    }

    private String formatFeedback(Feedback feedback) {
        return String.format(
         """
         📅 Дата: %s
         👤 Имя: %s
         📞 Телефон: %s
         ✉️ Email: %s
         📝 Текст: %s
         """,
         feedback.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
         feedback.getUserName(),
         feedback.getUserMobileNumber(),
         feedback.getUserEmail() != null ? feedback.getUserEmail() : "не указан",
         feedback.getUserRequestText() != null ?
          feedback.getUserRequestText().length() > 100 ?
           feedback.getUserRequestText().substring(0, 100) + "..." :
           feedback.getUserRequestText() :
          "без текста"
        );
    }
}