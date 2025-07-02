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
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionTemplate;

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

    @Autowired
    private PlatformTransactionManager transactionManager;

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

        if (update.callbackQuery() != null) {
            handleCallback(update.callbackQuery());
            return;
        }

        Message message = update.message();
        if (message == null) {
            return;
        }

        Long chatId = message.chat().id();

        if (chatId.equals(adminChatId)) {
            handleCommand(message);
        } else {
            sendNonAdminResponse(chatId);
        }
    }

    private void sendNonAdminResponse(Long chatId) {
        telegramBot.execute(new SendMessage(chatId, """
         Этот бот предназначен для сотрудников компании.
         Ваш chat_id: `%d`
         
         Для доступа к функциям бота обратитесь к разработчику, сообщив ему ваш chat_id
         """.formatted(chatId)));
    }

    private void handleCommand(Message message) {
        String text = message.text().trim();

        switch (text) {
            case "/start":
            case "🆘 Помощь":
                sendWelcomeMessage();
                break;
            case "/tasks":
            case "📋 Заявки":
                sendRecentConsultations();
                break;
            case "/feedbacks":
            case "📝 Обращения":
                sendRecentFeedbacks();
                break;
            default:
                sendMessage("Неизвестная команда", getMainMenuKeyboard());
        }
    }

    // Уведомления о новых данных
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNewConsultationTask(ConsultationTask task) {
        String message = "\uD83D\uDE80 *НОВАЯ ЗАЯВКА* \uD83D\uDE80\n" + formatConsultation(task);
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
         new InlineKeyboardButton("❌ Удалить").callbackData("delete_consult_" + task.getId())
        );
        sendMessage(message, keyboard);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNewFeedback(Feedback feedback) {
        String message = "\uD83C\uDF1F *НОВОЕ ОБРАЩЕНИЕ* \uD83C\uDF1F\n" + formatFeedback(feedback);
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
         new InlineKeyboardButton("❌ Удалить").callbackData("delete_feedback_" + feedback.getId())
        );
        sendMessage(message, keyboard);
    }

    // Основные методы работы с ботом
    private void sendWelcomeMessage() {
        String text = """
         🌿 Бот управления заявками HomePlant
         
         Доступные команды бота:
         
         /tasks - Показывает все оставленные на сайте заявки (имя заказчика и телефон)
         
         /feedbacks - Показывает все обращения (имя, телефон, email и текст обращения от заказчика)
         
         При поступлении новых заявков или обращений, я сразу опубликую их в чат, чтобы вы могли своевременно отслеживать заказы
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
            sendMessage("Нет обращений", getMainMenuKeyboard());
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
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        try {
            transactionTemplate.execute(status -> {
                if (data.startsWith("delete_consult_")) {
                    UUID id = UUID.fromString(data.substring(15));
                    consultationTaskService.deleteTask(id);
                    sendMessage("✅ Заявка удалена", getMainMenuKeyboard());
                } else if (data.startsWith("delete_feedback_")) {
                    UUID id = UUID.fromString(data.substring(16));
                    feedbackService.deleteFeedback(id);
                    sendMessage("✅ Обращение удалёно", getMainMenuKeyboard());
                }
                return null;
            });
            telegramBot.execute(new DeleteMessage(adminChatId, callback.message().messageId()));
        } catch (Exception e) {
            sendMessage("❌ Ошибка: " + e.getMessage());
        }
    }

    // Вспомогательные методы
    private ReplyKeyboardMarkup getMainMenuKeyboard() {
        return new ReplyKeyboardMarkup(
         new String[]{"📋 Заявки", "📝 Обращения"},
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