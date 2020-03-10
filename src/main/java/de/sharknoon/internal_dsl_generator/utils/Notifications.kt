package de.sharknoon.internal_dsl_generator.utils

import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant

fun showErrorNotification(s: String) {
    val notification = Notification(s)
    notification.addThemeVariants(NotificationVariant.LUMO_ERROR)
    notification.duration = 1000 * 5
    notification.open()
}
