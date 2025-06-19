document.addEventListener('DOMContentLoaded', function() {

    const API_URL = 'http://localhost:8080/api/consultation-tasks';

// Система уведомлений
class NotificationSystem {
  constructor() {
    this.notification = document.getElementById('notification');
    this.messageElement = document.getElementById('notification-message');
    this.closeButton = document.getElementById('notification-close');

    this.closeButton.addEventListener('click', () => this.hide());
  }

  show(message, type = 'success', duration = 5000) {
    this.messageElement.textContent = message;
    this.notification.classList.remove('hidden', 'success', 'error');
    this.notification.classList.add(type, 'visible');

    // Автоматическое закрытие
    if (this.timeoutId) clearTimeout(this.timeoutId);
    this.timeoutId = setTimeout(() => this.hide(), duration);
  }

  hide() {
    this.notification.classList.add('hidden');
    this.notification.classList.remove('visible');
  }
}

// Инициализация системы уведомлений
const notifier = new NotificationSystem();

// 2. Функции для работы с ошибками форм
    function showInputError(input, message) {
        // Удаляем предыдущие ошибки для этого поля
        const parent = input.parentNode;
        const existingError = parent.querySelector('.input-error');
        if (existingError) existingError.remove();

        // Создаем новый элемент ошибки
        const errorElement = document.createElement('div');
        errorElement.className = 'input-error';
        errorElement.textContent = message;
        errorElement.style.color = '#f44336';
        errorElement.style.fontSize = '0.8rem';
        errorElement.style.marginTop = '4px';

        parent.appendChild(errorElement);
        input.style.borderColor = '#f44336';
    }

    function resetFormErrors() {
        // Удаляем все ошибки
        document.querySelectorAll('.input-error').forEach(el => el.remove());

        // Сбрасываем стили полей
        document.querySelectorAll('#consult-form input').forEach(input => {
            input.style.borderColor = '';
        });
    }

    // Плавная прокрутка для якорных ссылок
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            e.preventDefault();

            const targetId = this.getAttribute('href');
            if (targetId === '#') return;

            const targetElement = document.querySelector(targetId);
            if (targetElement) {
                window.scrollTo({
                    top: targetElement.offsetTop - 80,
                    behavior: 'smooth'
                });
            }
        });
    });

    // Модальное окно
    const modal = document.getElementById('modal');
    const ctaButton = document.getElementById('cta-button');
    const closeButton = document.querySelector('.close');

    if (ctaButton) {
            ctaButton.addEventListener('click', function() {
                modal.style.display = 'block';
            });
        }

        if (closeButton) {
            closeButton.addEventListener('click', function() {
                modal.style.display = 'none';
            });
        }

        if (modal) {
            window.addEventListener('click', function(event) {
                if (event.target === modal) {
                    modal.style.display = 'none';
                }
            });
        }



    // Загрузка портфолио
    const portfolioGrid = document.getElementById('portfolio-grid');
    const portfolioItems = [
        {
            image: 'images/portfolio1.jpg',
            title: 'Небольшой сад в Подмосковье',
            description: 'Ландшафтный дизайн и озеленение'
        },
        {
            image: 'images/portfolio2.jpg',
            title: 'Альпийский сад',
            description: 'Оформление частного сада на участке'
        },
        {
            image: 'images/portfolio3.jpg',
            title: 'Оформление участка по вашим предложениям',
            description: 'Подборка деревьев и растений на ваш вкус'
        },
        {
            image: 'images/portfolio4.jpg',
            title: 'Облагораживание территории',
            description: 'Посадка деревьев и кустарников'
        },
        {
            image: 'images/portfolio5.jpg',
            title: 'Большой ассортимент',
            description: 'Только качественные растения'
        },
        {
            image: 'images/portfolio6.jpg',
            title: 'Деревья любых размеров',
            description: 'Подберём растения под ваш участок'
        }
    ];

    portfolioItems.forEach(item => {
        const portfolioItem = document.createElement('div');
        portfolioItem.className = 'portfolio-item';
        portfolioItem.innerHTML = `
            <img src="${item.image}" alt="${item.title}">
            <div class="overlay">
                <h3>${item.title}</h3>
                <p>${item.description}</p>
            </div>
        `;
        portfolioGrid.appendChild(portfolioItem);
    });

    // Обработка форм

   const consultForm = document.getElementById('consult-form');

      consultForm.addEventListener('submit', async function(e) {
          e.preventDefault();
          resetFormErrors();

          // Получаем данные формы
          const name = this.elements[0].value.trim();
          const phone = this.elements[1].value.trim();

          // Валидация
        let isValid = true;

        if (!name) {
          showInputError(this.elements[0], 'Пожалуйста, введите имя');
          isValid = false;
        }

        if (!phone) {
          showInputError(this.elements[1], 'Пожалуйста, введите телефон');
          isValid = false;
        } else if (!/^[\d+()\s-]{7,15}$/.test(phone)) {
          showInputError(this.elements[1], 'Неверный формат телефона');
          isValid = false;
        }

        if (!isValid) {
          notifier.show('Пожалуйста, исправьте ошибки в форме', 'error');
          return;
        }

          // Показываем состояние загрузки
          const submitBtn = this.querySelector('button');
          const originalBtnText = submitBtn.textContent;
          submitBtn.disabled = true;
          submitBtn.textContent = 'Отправка...';

          try {
              // Отправляем данные на бэкенд
              const response = await fetch(API_URL, {
                  method: 'POST',
                  headers: {
                      'Content-Type': 'application/json',
                  },
                  body: JSON.stringify({
                      userName: name,
                      rawPhoneNumber: phone
                  })
              });

            const responseData = await response.json();

             if (response.ok) {
                    notifier.show('Заявка успешно отправлена! Мы свяжемся с вами в ближайшее время', 'success');

                     this.reset();
                     modal.style.display = 'none';

                  } else {
                     const errorMsg = responseData.message || responseData.error || 'Ошибка сервера';
                  if (response.status === 400 && responseData.errors) {
                         Object.entries(responseData.errors).forEach(([field, message]) => {
                           const fieldIndex = field === "userName" ? 0 : 1;
                           showInputError(consultForm.elements[fieldIndex], message);
                         });
                         notifier.show('Исправьте ошибки в форме', 'error');
                       } else {
                         throw new Error(errorMsg);
                       }
                     }
                   } catch (error) {
                     // ОБРАБОТКА СЕТЕВЫХ ОШИБОК И ИСКЛЮЧЕНИЙ
                     console.error('Ошибка:', error);
                     notifier.show(`Ошибка: ${error.message}`, 'error');
                   } finally {
                     // ВОССТАНОВЛЕНИЕ КНОПКИ
                     submitBtn.disabled = false;
                     submitBtn.textContent = originalBtnText;
                   }
                 });


      // 5. Обработка основной контактной формы (локальная)
      const contactForm = document.getElementById('contact-form');

      if (contactForm) {
          contactForm.addEventListener('submit', function(e) {
              e.preventDefault();
              ('Спасибо за ваше сообщение! Мы свяжемся с вами в ближайшее время.');
              this.reset();
          });
      }


      // 6. Фиксированная шапка при прокрутке
      window.addEventListener('scroll', function() {
          const header = document.querySelector('header');
          if (window.scrollY > 100) {
              header.style.boxShadow = '0 2px 10px rgba(0,0,0,0.1)';
          } else {
              header.style.boxShadow = '0 2px 5px rgba(0,0,0,0.1)';
          }
      });
  });