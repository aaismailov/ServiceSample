# Задание:

1. Создать сэмпл проект с одной активити.
2. Написать сервис, который будет запускаться как bind service,  написать сервис, который будет запускаться как foreground service.
3. Наладить взаимодействие между активити и двумя сервисами, пусть сервисы через определенные промежутки времени отправляют в активити следующее число из прогрессии Фибоначи (0-1-1-2-3-5-8 - и так до бесконечности). А активити будет выводить это число на экран в текст вью. 
4. Добавить обработку случаев когда система может убить ваш сервис, чтобы сервис продолжал прогресс с момента на, котором остановился*