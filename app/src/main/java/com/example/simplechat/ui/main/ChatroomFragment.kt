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
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.simplechat.R
import com.example.simplechat.models.MessageModel
import kotlinx.android.synthetic.main.chatroom_fragment.view.*

class ChatroomFragment : Fragment() {

    var user: UserModel? = null
    lateinit var txtMessageBox: AppCompatEditText
    lateinit var btnSend: AppCompatImageView
    lateinit var recyclerMessages: RecyclerView
    lateinit var progressLoading: ProgressBar

    val messagesList = arrayListOf<MessageModel>()
    var messagesAdapter: ChatMessagesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.chatroom_fragment, container, false)
        // Set up the toolbar_menu.

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ChatroomViewModel::class.java)
        // TODO: Use the ViewModel

    }


    fun setupViews()
    {
        // Get Views
        txtMessageBox = findViewById(R.id.txtMessageBox)
        btnSend = findViewById(R.id.imgSend)
        recyclerMessages = findViewById(R.id.recyclerMessages)
        progressLoading = findViewById(R.id.progressLoading)

        // Toolbar
        supportActionBar?.apply {
            title = user?.name
            subtitle = user?.status
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
        setUserStatus(user?.status == "online")

        // Recycler View
        messagesAdapter = ChatMessagesAdapter()
        recyclerMessages.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerMessages.adapter = messagesAdapter

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
        btnSend.setOnClickListener {
            sendMessage(txtMessageBox.text.toString())
        }
    }

    private fun setUserStatus(isOnline: Boolean)
    {
        if (isOnline)
        {
            supportActionBar?.subtitle = Html.fromHtml("<font color='#149214'>online</font>")
        }
        else
        {
            supportActionBar?.subtitle = Html.fromHtml("<font color='#575757'>offline</font>")
        }
    }

    private fun sendMessage(message: String) {
        if (!message.isEmpty())
        {
            user?.let {
                val receiverID: String = it.uid
                val messageText = message

                var messageModel = MessageModel(message, true)
                messagesList.add(messageModel)
                messagesAdapter?.notifyItemInserted(messagesList.size-1)
                recyclerMessages.scrollToPosition(messagesList.size-1)

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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId)
        {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun loadDummyMessages()
    {
        messagesList.add(MessageModel("Hi", true))
        messagesList.add(MessageModel("How are you?", true))
        messagesList.add(MessageModel("I'm fine", false))
        messagesList.add(MessageModel("How about ya?", false))
        messagesList.add(MessageModel("Fine", true))
        messagesList.add(MessageModel("What ya upto these days?", true))
        messagesList.add(MessageModel("Same ol' job dude!", false))
        messagesList.add(MessageModel("Same ol' job dude! asfa sfasf asfd asfd asf asdf asfd asf asfasf asf asf asf sf safasfdasdf asf asfd asf asdfasdf", false))
        messagesList.add(MessageModel("Same ol' job dude! asfa sfasf asfd asfd asf asdf asfd asf asfasf asf asf asf sf safasfdasdf asf asfd asf asdfasdf", true))
        messagesAdapter?.notifyDataSetChanged()
    }


}
