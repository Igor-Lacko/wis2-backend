# Notification System Guide

This guide explains how to use the newly implemented notification system from the Frontend.

## Endpoints

### 1. Get User Notifications
Retrieves all notifications for the currently logged-in user, ordered by creation date (newest first).

*   **URL:** `/api/notifications`
*   **Method:** `GET`

### 2. Send Notification to User
Sends a notification to a specific user. Any user can send a notification to any other user.

*   **URL:** `/api/notifications/send`
*   **Method:** `POST`
*   **Headers:** `Content-Type: application/json`
*   **Body:**
    ```json
    {
        "recipientId": 123,
        "message": "Hello, this is a message."
    }
    ```

### 3. Send Course Notification
Sends a notification to all students enrolled in a specific course. Only the course supervisor, teachers of the course, or an ADMIN can perform this action.

*   **URL:** `/api/notifications/course/{courseId}`
*   **Method:** `POST`
*   **Headers:** `Content-Type: application/json`
*   **Body:**
    ```json
    {
        "message": "Important update regarding the exam."
    }
    ```

### 4. Mark Notification as Read
Marks a specific notification as read.

*   **URL:** `/api/notifications/{id}/read`
*   **Method:** `PUT`

## Frontend Usage Examples (TypeScript + fetch)

Here are helper functions you can use in your React/Vue/Angular application.

```typescript
const API_BASE_URL = 'http://localhost:8080/api'; // Adjust as needed

// Helper to get the token (implementation depends on your auth logic)
const getToken = () => localStorage.getItem('token');

interface Notification {
    id: number;
    senderId: number;
    senderName: string;
    message: string;
    createdAt: string;
    isRead: boolean;
    courseId?: number;
    courseName?: string;
}

// 1. Fetch Notifications
export const fetchNotifications = async (): Promise<Notification[]> => {
    const response = await fetch(`${API_BASE_URL}/notifications`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    });

    if (!response.ok) {
        throw new Error('Failed to fetch notifications');
    }

    return response.json();
};

// 2. Send Notification to User
export const sendNotification = async (recipientId: number, message: string): Promise<void> => {
    const response = await fetch(`${API_BASE_URL}/notifications/send`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ recipientId, message })
    });

    if (!response.ok) {
        throw new Error('Failed to send notification');
    }
};

// 3. Send Course Notification
export const sendCourseNotification = async (courseId: number, message: string): Promise<void> => {
    const response = await fetch(`${API_BASE_URL}/notifications/course/${courseId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message })
    });

    if (!response.ok) {
        throw new Error('Failed to send course notification');
    }
};

// 4. Mark as Read
export const markNotificationAsRead = async (notificationId: number): Promise<void> => {
    const response = await fetch(`${API_BASE_URL}/notifications/${notificationId}/read`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        }
    });

    if (!response.ok) {
        throw new Error('Failed to mark notification as read');
    }
};
```
