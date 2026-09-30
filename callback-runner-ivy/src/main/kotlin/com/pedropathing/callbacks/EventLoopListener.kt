package com.pedropathing.callbacks

import android.content.Context
import com.qualcomm.ftccommon.FtcEventLoop
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier
import org.firstinspires.ftc.ftccommon.external.OnCreateEventLoop

class EventLoopListener : OpModeManagerNotifier.Notifications {
    override fun onOpModePreInit(opMode: OpMode?) { callbacksMap.clear() }
    override fun onOpModePostStop(opMode: OpMode?) { callbacksMap.clear() }
    override fun onOpModePreStart(opMode: OpMode?) {}

    companion object {
        @JvmStatic
        @OnCreateEventLoop
        fun register(ctx: Context, eventLoop: FtcEventLoop) {
            eventLoop.opModeManager.registerListener(EventLoopListener())
        }
    }
}
