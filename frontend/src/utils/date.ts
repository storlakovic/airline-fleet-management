export const formatTime = (date: string | Date) =>
  new Date(date).toLocaleTimeString('en-GB', {
    hour: '2-digit',
    minute: '2-digit',
    timeZone: 'UTC',
  });
