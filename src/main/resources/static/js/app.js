// SmartApply - Client-side JavaScript

document.addEventListener('DOMContentLoaded', function() {
    console.log('SmartApply loaded! 🚀');

    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            alert.style.transition = 'opacity 0.5s ease-out';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // Add smooth scroll behavior
    document.documentElement.style.scrollBehavior = 'smooth';

    // Form validation enhancements
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Wird gespeichert...';
            }
        });
    });

    // Set today as default for application date if empty
    const applicationDateInput = document.getElementById('applicationDate');
    if (applicationDateInput && !applicationDateInput.value) {
        const today = new Date().toISOString().split('T')[0];
        applicationDateInput.value = today;
    }
});

// Confirm delete actions
function confirmDelete(message) {
    return confirm(message || 'Möchten Sie diesen Eintrag wirklich löschen?');
}