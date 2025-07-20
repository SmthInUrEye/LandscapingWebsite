document.addEventListener('DOMContentLoaded', function() {
    // ====================== КОНСТАНТЫ И ПЕРЕМЕННЫЕ ======================

    //Для локального запуска
    //const API_URL_TASKS = 'http://localhost:8080/api/consultation-tasks';
    //const API_URL_FEEDBAKS = 'http://localhost:8080/api/feedbacks';

    //Запуск через контейнер
    const API_URL_TASKS = '/api/consultation-tasks';
    const API_URL_FEEDBAKS = '/api/feedbacks';

    const modal = document.getElementById('modal');
    const consultForm = document.getElementById('consult-form');
    const contactForm = document.getElementById('contact-form');
    let startX, startY; // Координаты начала взаимодействия
    let isMouseDownInside = false;

    // ====================== СИСТЕМА УВЕДОМЛЕНИЙ ======================
    class NotificationSystem {
        constructor() {
            this.modal = document.getElementById('notification-modal');
            this.messageElement = document.getElementById('notification-message');
            this.closeButton = document.getElementById('notification-close');
            this.timeoutId = null;

            this.initEventListeners();
        }

        initEventListeners() {
            this.closeButton.addEventListener('click', () => this.hide());
            this.modal.addEventListener('click', (e) => {
                if (e.target === this.modal) this.hide();
            });
        }

        show(message, type = 'success') {
            this.messageElement.textContent = message;
            this.modal.classList.add('active');

            const content = this.modal.querySelector('.notification-content');
            content.className = 'notification-content';
            content.classList.add(type);

            if (type === 'success') {
                if (this.timeoutId) clearTimeout(this.timeoutId);
                this.timeoutId = setTimeout(() => this.hide(), 5000);
            }
        }

        hide() {
            this.modal.classList.remove('active');
            if (this.timeoutId) clearTimeout(this.timeoutId);
        }
    }

    const notifier = new NotificationSystem();

    // ====================== ФУНКЦИИ ДЛЯ РАБОТЫ С ФОРМАМИ ======================
    function showInputError(input, message) {
        const parent = input.parentNode;
        const existingError = parent.querySelector('.input-error');

        if (existingError) existingError.remove();

        const errorElement = document.createElement('div');
        errorElement.className = 'input-error';
        errorElement.textContent = message;
        errorElement.style.cssText = 'color: #f44336; font-size: 0.8rem; margin-top: 4px;';

        parent.appendChild(errorElement);
        input.classList.add('input-error-style');
    }

    function resetFormErrors() {
        document.querySelectorAll('.input-error').forEach(el => el.remove());
        document.querySelectorAll('input, textarea').forEach(input => {
            input.classList.remove('input-error-style');
        });
    }

    // ====================== НАСТРОЙКА МОДАЛЬНОГО ОКНА ======================
   function setupModal() {
       if (!modal) return;

       let isInteractingWithContent = false;

       // Обработчики открытия/закрытия
       if (document.getElementById('cta-button')) {
           document.getElementById('cta-button').addEventListener('click', () => {
               modal.style.display = 'block';
           });
       }

       if (document.querySelector('.close')) {
           document.querySelector('.close').addEventListener('click', () => {
               modal.style.display = 'none';
           });
       }

       // Обработчики для всех интерактивных элементов внутри modal-content
       const modalContent = modal.querySelector('.modal-content');
       if (modalContent) {
           // Добавляем обработчики для всех возможных элементов взаимодействия
           const interactiveElements = modalContent.querySelectorAll(
               'h2, p, a, input, textarea, button, [tabindex], .interactive'
           );

           interactiveElements.forEach(element => {
               // При начале взаимодействия
               element.addEventListener('mousedown', () => {
                   isInteractingWithContent = true;
               });

               // При окончании взаимодействия
               element.addEventListener('mouseup', () => {
                   // Небольшая задержка для случаев выделения текста
                   setTimeout(() => {
                       isInteractingWithContent = false;
                   }, 100);
               });
           });

           // Запрещаем всплытие событий от полей формы
           modalContent.querySelectorAll('input, textarea, button').forEach(element => {
               element.addEventListener('mousedown', (e) => {
                   e.stopPropagation();
               });
           });
       }

       // Обработчик клика по оверлею
       modal.addEventListener('click', function(e) {
           if (e.target === modal && !isInteractingWithContent) {
               modal.style.display = 'none';
           }
       });
   }

    // ====================== ПОРТФОЛИО ======================
    function loadPortfolio() {
        const portfolioGrid = document.getElementById('portfolio-grid');
        if (!portfolioGrid) return;

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
                <img src="${item.image}" alt="${item.title}" loading="lazy">
                <div class="overlay">
                    <h3>${item.title}</h3>
                    <p>${item.description}</p>
                </div>
            `;
            portfolioGrid.appendChild(portfolioItem);
        });
    }

    // ====================== ОБРАБОТКА ФОРМ ======================
 function setupConsultForm() {
     if (!consultForm) return;

     consultForm.addEventListener('submit', async function(e) {
         e.preventDefault();
         resetFormErrors();

         // Получаем данные формы
         const formData = {
             userName: this.elements.userName.value.trim(),
             rawPhoneNumber: this.elements.rawPhoneNumber.value.trim()
         };

         // Валидация на клиенте
         let isValid = true;

         if (!formData.userName) {
             showInputError(this.elements.userName, 'Имя обязательно');
             isValid = false;
         }

         if (!formData.rawPhoneNumber) {
             showInputError(this.elements.rawPhoneNumber, 'Номер телефона обязателен');
             isValid = false;
         }

         if (!isValid) {
             notifier.show('Пожалуйста, заполните все обязательные поля', 'error');
             return;
         }

         // Подготовка к отправке
         const submitBtn = this.querySelector('button[type="submit"]');
         const originalBtnText = submitBtn.textContent;
         submitBtn.disabled = true;
         submitBtn.textContent = 'Отправка...';

         // Настройка таймаута (10 секунд)
         const controller = new AbortController();
         const timeoutId = setTimeout(() => controller.abort(), 10000);

         try {
             // Отправка запроса
             const response = await fetch(API_URL_TASKS, {
                 method: 'POST',
                 headers: {
                     'Content-Type': 'application/json'
                 },
                 body: JSON.stringify(formData),
                 signal: controller.signal
             });

             clearTimeout(timeoutId);

             // Обработка успешного ответа
             if (response.ok) {
                 notifier.show('Заявка успешно отправлена! Мы свяжемся с вами в ближайшее время', 'success');
                 this.reset();
                 modal.style.display = 'none';
                 return;
             }

             // Попытка прочитать ответ как текст
             const responseText = await response.text();
             console.log('Raw server response:', responseText);

             // Попытка парсинга как JSON (если возможно)
             try {
                 const errorData = JSON.parse(responseText);
                 if (errorData.errors) {
                     // Обработка ошибок валидации
                     Object.entries(errorData.errors).forEach(([field, message]) => {
                         const input = this.elements[field];
                         if (input) showInputError(input, message);
                     });
                     notifier.show('Пожалуйста, исправьте ошибки в форме', 'error');
                 } else if (errorData.message) {
                     // Обработка сообщения об ошибке
                     notifier.show(errorData.message, 'error');
                 } else {
                     notifier.show(responseText || `Ошибка сервера: ${response.status}`, 'error');
                 }
             } catch (jsonError) {
                 // Если не JSON, показываем текст ответа как есть
                 console.log('Response is not JSON, showing raw text');
                 notifier.show(responseText || `Ошибка сервера: ${response.status}`, 'error');
             }

         } catch (error) {
             clearTimeout(timeoutId);
             console.error('Request failed:', error);

             if (error.name === 'AbortError') {
                 notifier.show('Сервер не отвечает. Попробуйте позже', 'error');
             } else if (error instanceof SyntaxError) {
                 notifier.show('Ошибка обработки ответа сервера', 'error');
             } else if (error instanceof TypeError) {
                 notifier.show('Проблемы с подключением. Проверьте интернет', 'error');
             } else {
                 notifier.show('Произошла непредвиденная ошибка', 'error');
             }
         } finally {
             submitBtn.disabled = false;
             submitBtn.textContent = originalBtnText;
         }
     });
 }

    function setupContactForm() {
        if (!contactForm) return;

        contactForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            resetFormErrors();

            // Получаем данные формы
            const formData = {
                userName: this.elements.userName.value.trim(),
                rawPhoneNumber: this.elements.rawPhoneNumber.value.trim(),
                email: this.elements.email.value.trim(),
                message: this.elements.message.value.trim()
            };

            // Валидация на клиенте
            let isValid = true;

            if (!formData.userName) {
                showInputError(this.elements.userName, 'Имя обязательно');
                isValid = false;
            }

            if (!formData.rawPhoneNumber) {
                showInputError(this.elements.rawPhoneNumber, 'Телефон обязателен');
                isValid = false;
            }

            if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
                showInputError(this.elements.email, 'Некорректный формат email');
                isValid = false;
            }

            if (!isValid) {
                notifier.show('Пожалуйста, заполните обязательные поля', 'error');
                return;
            }

            // Показываем состояние загрузки
            const submitBtn = this.querySelector('button[type="submit"]');
            const originalBtnText = submitBtn.textContent;
            submitBtn.disabled = true;
            submitBtn.textContent = 'Отправка...';

            // Настройка таймаута (10 секунд)
            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), 10000);

            try {
                const response = await fetch(API_URL_FEEDBAKS, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        userName: formData.userName,
                        rawPhoneNumber: formData.rawPhoneNumber,
                        rawEmail: formData.email || null,
                        userRequestText: formData.message
                    }),
                    signal: controller.signal // Подключаем AbortController
                });

                clearTimeout(timeoutId); // Очищаем таймаут при успешном ответе

                const contentType = response.headers.get('content-type');
                let responseData;

                // Обрабатываем разные форматы ответа
                if (contentType && contentType.includes('application/json')) {
                    responseData = await response.json();
                } else {
                    const text = await response.text();
                    try {
                        responseData = JSON.parse(text); // Пробуем распарсить как JSON
                    } catch {
                        responseData = { message: text }; // Используем текст как сообщение
                    }
                }

                if (response.ok) {
                    notifier.show('Сообщение отправлено!', 'success');
                    this.reset();
                } else {
                    if (responseData.errors) {
                        Object.entries(responseData.errors).forEach(([field, message]) => {
                            const input = this.elements[field];
                            if (input) showInputError(input, message);
                        });
                    }
                    notifier.show(responseData.message || `Ошибка: ${response.status}`, 'error');
                }

            } catch (error) {
                clearTimeout(timeoutId); // Очищаем таймаут при ошибке

                if (error.name === 'AbortError') {
                    notifier.show('Превышено время ожидания ответа от сервера', 'error');
                } else if (error instanceof TypeError) {
                    notifier.show('Ошибка сети. Проверьте подключение', 'error');
                } else {
                    console.error('Ошибка:', error);
                    notifier.show('Произошла непредвиденная ошибка', 'error');
                }
            } finally {
                submitBtn.disabled = false;
                submitBtn.textContent = originalBtnText;
            }
        });
    }

    // ====================== ДОПОЛНИТЕЛЬНЫЕ ФУНКЦИИ ======================
    function setupSmoothScroll() {
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
    }

    function setupStickyHeader() {
        window.addEventListener('scroll', function() {
            const header = document.querySelector('header');
            if (!header) return;

            header.style.boxShadow = window.scrollY > 100
                ? '0 2px 10px rgba(0,0,0,0.1)'
                : '0 2px 5px rgba(0,0,0,0.1)';
        });
    }

    function setupBurger() {
    document.querySelector('.burger').addEventListener('click', () => {
        document.querySelector('nav ul').classList.toggle('active');
    });
    }

    // ====================== ИНИЦИАЛИЗАЦИЯ ======================
    function init() {
        setupModal();
        loadPortfolio();
        setupConsultForm();
        setupContactForm();
        setupSmoothScroll();
        setupStickyHeader();
        setupBurger();
    }

    init();
});