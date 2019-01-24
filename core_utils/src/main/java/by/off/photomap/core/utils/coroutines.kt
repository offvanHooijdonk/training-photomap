package by.off.photomap.core.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Runs new scope on IO and launches a coroutine inside
 */
fun launchScopeIO(block: suspend CoroutineScope.() -> Unit) = CoroutineScope(Dispatchers.IO).launch(block = block)