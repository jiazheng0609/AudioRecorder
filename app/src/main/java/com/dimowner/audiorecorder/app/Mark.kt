package com.dimowner.audiorecorder.app

import android.content.Context
import android.util.Log
import com.dimowner.audiorecorder.ARApplication
import com.dimowner.audiorecorder.AppConstants
import com.dimowner.audiorecorder.data.Prefs
import com.dimowner.audiorecorder.util.FileUtil
import com.dimowner.audiorecorder.util.TimeUtils
import java.io.File

class Mark {

    private lateinit var fileSrt: File
    private lateinit var fileEdl: File
    private var contentOut: String = ""
    private var _name: String = ""
    private var counter: Int = 0

    private var timecodes: MutableList<Long> = mutableListOf()
    private var colors: MutableList<String> = mutableListOf()
    private var msgs: MutableList<String> = mutableListOf()

    private lateinit var prefs: Prefs
    private var FPS = 30

    fun startMark(context: Context, name: String) {
        _name = name
        var filenameExt = name + ".srt"
        var path = FileUtil.getPrivateRecordsDir(context).absolutePath +  filenameExt
        fileSrt = FileUtil.createFile(FileUtil.getPrivateRecordsDir(context), filenameExt)
        Log.d("jiazheng.mark", "mark start called " + path)

        filenameExt = name + ".edl"
        path = FileUtil.getPrivateRecordsDir(context).absolutePath +  filenameExt
        fileEdl = FileUtil.createFile(FileUtil.getPrivateRecordsDir(context), filenameExt)
        Log.d("jiazheng.mark", "mark start called " + path)

        prefs = ARApplication.getInjector().providePrefs()

    }
    fun addMarkPoint(timeMills: Long, color: String, msg: String) {
        counter = counter + 1
        timecodes.add(timeMills)
        colors.add(color)
        msgs.add(msg)
    }

    fun finishEdlMark(name: String) {
        var timeStart: String
        var timeEnd: String
        contentOut = "TITLE: " + name + '\n' + "FCM: NON-DROP FRAME\n\n"

        when (prefs.getSettingFPS()) {
            AppConstants.FPS_25 -> FPS = 25
            AppConstants.FPS_30 -> FPS = 30
            AppConstants.FPS_60 -> FPS = 60
            AppConstants.FPS_120 -> FPS = 120
        }

        for (i in 0..(counter-1)) {
            timeStart = TimeUtils.formatSMPTETime(timecodes[i], FPS)
            timeEnd = TimeUtils.formatSMPTETime(timecodes[i]+1*1000/FPS, FPS)
            contentOut = contentOut + String.format("%03d", i + 1) + " 001 V     C        " + timeStart + " " + timeEnd + " "+ timeStart + " " + timeEnd + '\n' + msgs[i] + " |C:ResolveColor" + colors[i] +" |M: |D:1" + "\n\n"
        }
    }

    fun finishSrtMark(name: String) {
        var timeStart: String
        var timeEnd: String
        for (i in 0..(counter-1)) {
            timeStart = TimeUtils.formatSrtTime(timecodes[i])
            timeEnd = TimeUtils.formatSrtTime(timecodes[i]+1)
            contentOut = contentOut + (i+1).toString() + '\n' + timeStart + " --> " + timeEnd  + '\n' + msgs[i] + "\n\n"
        }

    }

    fun finishMark(name: String) {
        finishSrtMark(name)
        fileSrt.writeText(contentOut)
        contentOut = ""

        finishEdlMark(name)
        fileEdl.writeText(contentOut)
        contentOut = ""

        if (name != _name)
            renameMark(name)

        _name = ""
        counter = 0
    }

    fun renameMark(newName: String) {
        if (FileUtil.renameFile(fileSrt, newName, "srt")) {
            Log.d("jiazheng.mark", "rename " + fileSrt.absolutePath + " to " + newName)
        }
        if (FileUtil.renameFile(fileEdl, newName, "edl")) {
            Log.d("jiazheng.mark", "rename " + fileSrt.absolutePath + " to " + newName)
        }
    }

}