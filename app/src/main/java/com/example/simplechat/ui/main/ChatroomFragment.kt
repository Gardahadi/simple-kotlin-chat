package com.example.simplechat.ui.main

import android.app.Activity
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Adapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechat.R


import com.example.simplechat.models.MessageModel
import com.example.simplechat.models.UserModel
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.chatroom_fragment.*
import kotlinx.android.synthetic.main.chatroom_fragment.view.*
import kotlinx.android.synthetic.main.chatroom_fragment.view.txt_message_box

class ChatroomFragment : Fragment() {
    companion object {
        fun newInstance() = ChatroomFragment()
    }



    var user: UserModel? = null
    lateinit var txtMessageBox: AppCompatEditText
    lateinit var btnSend: AppCompatImageView
    lateinit var recyclerMessages: RecyclerView
    lateinit var progressLoading: ProgressBar

    val messageList = ArrayList<MessageModel>()
    var messageAdapter : ChatMessagesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.chatroom_fragment, container, false)
        // Set up the toolbar_menu.
        // Write a message to the database
        val database = Firebase.database
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")
        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        user = UserModel (
            uid = "1",
            name = "Mail",
            status = "Last Active Yesterday",
            photoUrl = ""
        )
    }

    override fun onResume() {
        super.onResume()
        setupViews()
        loadConversationMessages()
    }


    fun setupViews()
    {
        // Get Views
        txtMessageBox = txt_message_box
        btnSend = img_send
        recyclerMessages = recycler_messages
        progressLoading = progress_loading

        // Toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        setUserStatus(user?.status == "online")


        // Recycler View
        messageAdapter = ChatMessagesAdapter()
        recyclerMessages.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        recyclerMessages.adapter = messageAdapter

        // Message Box
        txtMessageBox.setOnEditorActionListener { v, actionId, event ->
            when(actionId)
            {
                EditorInfo.IME_ACTION_GO -> {
                    sendMessage(txtMessageBox.text.toString())
                    return@setOnEditorActionListener true
                }
            }
            return@setOnEditorActionListener false
        }

        // Send Button
        val toast = Toast.makeText(this.context, "sent message", 2)
        btnSend.setOnClickListener {
            toast.show()
            sendMessage(txtMessageBox.text.toString())
        }
    }

    private fun setUserStatus(isOnline: Boolean)
    {
        if (isOnline)
        {
            (activity as AppCompatActivity).supportActionBar?.subtitle = Html.fromHtml("<font color='#149214'>online</font>")
        }
        else
        {
            (activity as AppCompatActivity).supportActionBar?.subtitle = Html.fromHtml("<font color='#575757'>offline</font>")
        }
    }

    private fun sendMessage(message: String) {
        if (!message.isEmpty())
        {
            user?.let {
                val receiverID: String = it.uid
                val messageText = message

                val messageModel = MessageModel(message, true)
                messageList.add(messageModel)
                messageAdapter?.notifyItemInserted(messageList.size-1)
                recyclerMessages.scrollToPosition(messageList.size-1)

                // Clear the message box
                txtMessageBox.setText("")
            }
        }
    }

    fun loadConversationMessages()
    {
        user?.let {

            // Show Progress Bar
            progressLoading.visibility = View.VISIBLE
            recyclerMessages.visibility = View.GONE

            Handler().postDelayed(Runnable {
                loadDummyMessages()

                // Hide Progress bar
                progressLoading.visibility = View.GONE
                recyclerMessages.visibility = View.VISIBLE

            }, 1*1000)          // 1 seconds dummy delay
        }
    }


    fun loadDummyMessages()
    {
        messageList.add(MessageModel("Hi", true))
        messageList.add(MessageModel("How are you?", true))
        messageList.add(MessageModel("I'm fine", false))
        messageList.add(MessageModel("How about ya?", false))
        messageList.add(MessageModel("Fine", true))
        messageList.add(MessageModel("What ya upto these days?", true))
        messageList.add(MessageModel("Same ol' job dude!", false))
        messageList.add(MessageModel("Same ol' job dude! asfa sfasf asfd asfd asf asdf asfd asf asfasf asf asf asf sf safasfdasdf asf asfd asf asdfasdf", false))
        messageList.add(MessageModel("Same ol' job dude! asfa sfasf asfd asfd asf asdf asfd asf asfasf asf asf asf sf safasfdasdf asf asfd asf asdfasdf", true))
        messageAdapter?.notifyDataSetChanged()
    }

    // Recycler Adapter
    inner class ChatMessagesAdapter : RecyclerView.Adapter<ChatMessagesAdapter.MessageViewHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            var view = LayoutInflater.from(context).inflate(R.layout.chat_message_content, parent, false)
            return MessageViewHolder(view)
        }

        override fun getItemCount(): Int = messageList.size

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            var messageModel = messageList[position]
            holder.bindItem(messageModel)
        }

        // View Holder
        inner class MessageViewHolder : RecyclerView.ViewHolder
        {
            var txtMyMessage: AppCompatTextView
            var txtOtherMessage: AppCompatTextView
            var cardMyMessage: MaterialCardView
            var cardOtherMessage: MaterialCardView

            constructor(itemView: View) : super(itemView)
            {
                txtMyMessage = itemView.findViewById(R.id.txtMyMessage)
                txtOtherMessage = itemView.findViewById(R.id.txtOtherMessage)
                cardMyMessage = itemView.findViewById(R.id.cardChatMyMessage)
                cardOtherMessage = itemView.findViewById(R.id.cardChatOtherMessage)
            }


            fun bindItem(messageModel: MessageModel)
            {
                if (messageModel.isMine)
                {
                    cardMyMessage.visibility = View.VISIBLE
                    cardOtherMessage.visibility = View.GONE
                    txtMyMessage.text = messageModel.message
                }
                else
                {
                    cardMyMessage.visibility = View.GONE
                    cardOtherMessage.visibility = View.VISIBLE
                    txtOtherMessage.text = messageModel.message
                }
            }
        }
    }

}
