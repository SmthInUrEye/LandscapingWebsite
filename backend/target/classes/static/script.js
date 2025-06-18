document.addEventListener('DOMContentLoaded', function() {
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

    ctaButton.addEventListener('click', function() {
        modal.style.display = 'block';
    });

    closeButton.addEventListener('click', function() {
        modal.style.display = 'none';
    });

    window.addEventListener('click', function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });

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
    const contactForm = document.getElementById('contact-form');
    const consultForm = document.getElementById('consult-form');

    if (contactForm) {
        contactForm.addEventListener('submit', function(e) {
            e.preventDefault();
            alert('Спасибо за ваше сообщение! Мы свяжемся с вами в ближайшее время.');
            this.reset();
        });
    }

    if (consultForm) {
        consultForm.addEventListener('submit', function(e) {
            e.preventDefault();
            alert('Спасибо за заявку! Наш менеджер свяжется с вами для консультации.');
            this.reset();
            modal.style.display = 'none';
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