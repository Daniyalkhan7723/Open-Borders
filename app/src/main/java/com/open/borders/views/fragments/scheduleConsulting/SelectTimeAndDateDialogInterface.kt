package com.open.borders.views.fragments.scheduleConsulting



interface SelectTimeAndDateDialogInterface {
        fun onSaveDateClick(date: String, timeZone: String)
        fun onSaveTimeClick(time: String, dateTime: String)
        fun onContinueConsultationClick(type: String, consultationWith: String)



}