 document.addEventListener('DOMContentLoaded', function() {
        const API_URL = 'http://localhost:8080/api/consultation-tasks';
        const modal = document.getElementById('modal');
        const consultForm = document.getElementById('consult-form');
        const contactForm = document.getElementById('contact-form');

        let startX, startY; // Координаты начала взаимодействия
        let isMouseDownInside = false;


        // Система уведомлений
        class NotificationSystem {
            constructor() {
                this.modal = document.getElementById('notification-modal');
                this.messageElement = document.getElementById('notification-message');
                this.closeButton = document.getElementById('notification-close');

                // Обработчики закрытия
                this.closeButton.addEventListener('click', () => this.hide());
                this.modal.addEventListener('click', (e) => {
                    if (e.target === this.modal) this.hide();
                });
            }

            show(message, type = 'success') {
                this.messageElement.textContent = message;
                this.modal.classList.add('active');

                // Обновляем стиль в зависимости от типа
                const content = this.modal.querySelector('.notification-content');
                content.className = 'notification-content';
                content.classList.add(type);

                // Автоматическое скрытие для успешных сообщений
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

        // Функции для работы с ошибками форм
        function showInputError(input, message) {
            const parent = input.parentNode;
            const errorId = `error-${input.name}`;

            // Удаляем предыдущие ошибки для этого поля
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
            input.classList.add('input-error-style');
        }

        function resetFormErrors() {
            // Удаляем все ошибки
            document.querySelectorAll('.input-error').forEach(el => el.remove());

            // Сбрасываем стили полей
            document.querySelectorAll('input, textarea').forEach(input => {
                input.classList.remove('input-error-style');
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
        if (document.getElementById('cta-button')) {
            document.getElementById('cta-button').addEventListener('click', function() {
                modal.style.display = 'block';
            });
        }

        if (document.querySelector('.close')) {
            document.querySelector('.close').addEventListener('click', function() {
                modal.style.display = 'none';
            });
        }

        if (modal) {
            modal.addEventListener('mousedown', function(e) {
            if (e.target.closest('.modal-content')) {
                startX = e.clientX;
                startY = e.clientY;
                isMouseDownInside = true;
            }
        });


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
                <img src="${item.image}" alt="${item.title}" loading="lazy">
                <div class="overlay">
                    <h3>${item.title}</h3>
                    <p>${item.description}</p>
                </div>
            `;
            portfolioGrid.appendChild(portfolioItem);
        });

        // Обработка формы консультации
       consultForm.addEventListener('submit', async function(e) {
             e.preventDefault();
             resetFormErrors();

             // Получаем данные формы
             const name = this.elements.userName.value.trim();
             const phone = this.elements.rawPhoneNumber.value.trim();

             // Валидация
             let isValid = true;

             if (!name) {
                 showInputError(this.elements.userName, 'Пожалуйста, введите имя');
                 isValid = false;
             }

             if (!phone) {
                 showInputError(this.elements.rawPhoneNumber, 'Пожалуйста, введите телефон');
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

                 // Определяем тип контента
                 const contentType = response.headers.get('content-type');
                 let errorMessage = 'Ошибка при отправке формы';

                 if (response.ok) {
                     notifier.show('Заявка успешно отправлена! Мы свяжемся с вами в ближайшее время', 'success');

                     // Сбрасываем форму и закрываем модальное окно
                     this.reset();
                     modal.style.display = 'none';

                 } else {
                     if (contentType && contentType.includes('application/json')) {
                         // Если ответ в формате JSON
                         const errorData = await response.json();
                         errorMessage = errorData.message || errorData.error || errorMessage;

                         // Обработка ошибок валидации
                         if (response.status === 400 && errorData.errors) {
                             Object.entries(errorData.errors).forEach(([field, message]) => {
                                 const input = this.elements[field];
                                 if (input) showInputError(input, message);
                             });
                         }
                     } else {
                         // Если ответ в текстовом формате
                         errorMessage = await response.text();
                     }

                     notifier.show(errorMessage, 'error');
                 }
             } catch (error) {
                 console.error('Ошибка:', error);
                 notifier.show('Сетевая ошибка. Проверьте подключение к интернету', 'error');
             } finally {
                 // Восстанавливаем кнопку
                 submitBtn.disabled = false;
                 submitBtn.textContent = originalBtnText;
             }
       });

        // Обработка основной контактной формы
        if (contactForm) {
            contactForm.addEventListener('submit', function(e) {
                e.preventDefault();
                notifier.show('Спасибо за ваше сообщение! Мы свяжемся с вами в ближайшее время', 'success');
                this.reset();
            });
        }

        // Фиксированная шапка при прокрутке
        window.addEventListener('scroll', function() {
            const header = document.querySelector('header');
            if (window.scrollY > 100) {
                header.style.boxShadow = '0 2px 10px rgba(0,0,0,0.1)';
            } else {
                header.style.boxShadow = '0 2px 5px rgba(0,0,0,0.1)';
            }
        });
    });