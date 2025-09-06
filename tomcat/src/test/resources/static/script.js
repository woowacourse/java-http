document.addEventListener("DOMContentLoaded", () => {
    const message = document.createElement("p");
    message.textContent = "Hello from test.js!";
    message.style.color = "blue";
    document.body.appendChild(message);
});
