document.querySelectorAll('.book-card').forEach(card => {
    card.addEventListener('click', (e) => {
        if (e.target.tagName === 'A' || e.target.tagName === 'BUTTON') {
            return;
        }
        const url = card.getAttribute('data-href');
        if (url) {
            window.location.href = url;
        }
    });
});