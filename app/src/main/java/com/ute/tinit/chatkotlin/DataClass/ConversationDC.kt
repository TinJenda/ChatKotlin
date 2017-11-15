package com.ute.tinit.chatkotlin.DataClass

/**
 * Created by tin3p on 11/11/2017.
 */
data class ConversationDC(var idConver:String?=null,var conversationName:String?=null, var listUsers:ArrayList<String>?=null, var isGroup:Boolean?=null,var listMessage:ArrayList<String>?=null)