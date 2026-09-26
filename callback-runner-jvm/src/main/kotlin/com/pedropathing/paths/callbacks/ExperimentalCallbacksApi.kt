package com.pedropathing.paths.callbacks

/**
 * Marks the global callback runner as experimental.
 *
 * The global [CallbackRunner] is expected to be deprecated with the arrival of modules for the major
 * command-based frameworks.
 */
@RequiresOptIn(message = "The global runner is experimental", level = RequiresOptIn.Level.WARNING)
annotation class ExperimentalCallbacksApi