package com.example.simplechat.ui.main


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechat.R
import com.example.simplechat.interfaces.NavigationHost
import com.example.simplechat.models.MessageModel
import com.example.simplechat.models.UserModel
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.chatroom_fragment.*
import kotlinx.android.synthetic.main.toolbar_content.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ChatroomFragment : Fragment() {
    companion object {
        fun newInstance() = ChatroomFragment()
    }

    // Initialize view variables (TO DO : Implement in ViewModel)
    var user: UserModel? = null

    lateinit var txtMessageBox: AppCompatEditText
    lateinit var btnSend: AppCompatImageView
    lateinit var btnLogout : AppCompatButton
    lateinit var recyclerMessages: RecyclerView
    lateinit var progressLoading: ProgressBar

    val messageList = ArrayList<MessageModel>()
    var messageAdapter : ChatMessagesAdapter? = null

    val database = Firebase.database
    val databaseRef = database.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.chatroom_fragment, container, false)
        // Set up the toolbar_menu.
        // Write a message to the database
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        user = UserModel (
            uid = "1",
            name = "Mail bin Ismail",
            status = "Last Active Yesterday",
            photoUrl = ""
        )
        setupViews()
        initConversationMessages()
        initAvatar()
    }

//    override fun onResume() {
//        super.onResume()
//
//    }


    fun setupViews()
    {
        // Get Views
        txtMessageBox = txt_message_box
        btnSend = img_send
        btnLogout = logout_button
        recyclerMessages = recycler_messages
        progressLoading = progress_loading

        // Set-up Toolbar
        var chatToolbar : Toolbar? = toolbar
        chatToolbar?.inflateMenu(R.menu.toolbar_menu)
        (activity as AppCompatActivity).setSupportActionBar(chatToolbar)

        // Set-up Logout Button
        btnLogout.setOnClickListener{
            (activity as NavigationHost).navigateTo(LoginFragment(), false)
        }

        // Set-up Recycler View
        messageAdapter = ChatMessagesAdapter()
        recyclerMessages.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        recyclerMessages.adapter = messageAdapter

        // Set-up Message Box
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

        // Set-up Send Button
        val toast = Toast.makeText(this.context, "sent message", 2)
        btnSend.setOnClickListener {
            toast.show()
            sendMessage(txtMessageBox.text.toString())
        }
    }

    private fun sendMessage(message: String) {
        if (!message.isEmpty())
        {
            user?.let {
                // Get Current Date
                val currentDate = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                val formattedDate = currentDate.format(formatter)
                val messageId : String = (messageList.size+1).toString()
                //Write Message to firebase
                val messageToWriteRef : DatabaseReference = databaseRef.child("Messages").child(messageId)
                messageToWriteRef.setValue(MessageModel(message, "1", formattedDate))

//                val messageModel = MessageModel(message, "1", formattedDate)
//                messageList.add(messageModel)
//                messageAdapter?.notifyItemInserted(messageList.size-1)
//                recyclerMessages.scrollToPosition(messageList.size-1)

                // Clear the message box
                txtMessageBox.setText("")
            }
        }
    }

    fun initConversationMessages()
    {
        user?.let {

            // Show Progress Bar
            progressLoading.visibility = View.VISIBLE
            recyclerMessages.visibility = View.GONE

            Handler().postDelayed(Runnable {
                //  Read messages from firebase
                initMessages()

                // Hide Progress bar
                progressLoading.visibility = View.GONE
                recyclerMessages.visibility = View.VISIBLE

            }, 1*1000)          // 1 seconds dummy delay
        }
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
            var dateMyMessage: AppCompatTextView
            var dateOtherMessage: AppCompatTextView
            var cardMyMessage: MaterialCardView
            var cardOtherMessage: MaterialCardView

            constructor(itemView: View) : super(itemView)
            {
                txtMyMessage = itemView.findViewById(R.id.txtMyMessage)
                txtOtherMessage = itemView.findViewById(R.id.txtOtherMessage)
                cardMyMessage = itemView.findViewById(R.id.cardChatMyMessage)
                cardOtherMessage = itemView.findViewById(R.id.cardChatOtherMessage)
                dateMyMessage = itemView.findViewById(R.id.userDate)
                dateOtherMessage = itemView.findViewById(R.id.otherDate)
            }


            fun bindItem(messageModel: MessageModel)
            {
                if (messageModel.sender_id == "1")
                {
                    cardMyMessage.visibility = View.VISIBLE
                    dateMyMessage.visibility = View.VISIBLE
                    cardOtherMessage.visibility = View.GONE
                    dateOtherMessage.visibility = View.GONE
                    txtMyMessage.text = messageModel.content
                    dateMyMessage.text = messageModel.date
                }
                else
                {
                    cardMyMessage.visibility = View.GONE
                    dateMyMessage.visibility = View.GONE
                    dateOtherMessage.visibility = View.VISIBLE
                    cardOtherMessage.visibility = View.VISIBLE
                    txtOtherMessage.text = messageModel.content
                    dateOtherMessage.text = messageModel.date
                }
            }
        }
    }

    private fun initMessages() {
        val messageListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messageList.clear()
                dataSnapshot.children.mapNotNullTo(messageList) {
                    it.getValue<MessageModel>(MessageModel::class.java)

                }
                messageAdapter?.notifyDataSetChanged()
                recyclerMessages.scrollToPosition(messageList.size-1)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        databaseRef.child("Messages").addValueEventListener(messageListener)
    }
    private fun initAvatar() {
        val avatarListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("PRINT_AVATAR", dataSnapshot.child("avatar").value.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        databaseRef.child("Users").child("2").addListenerForSingleValueEvent(avatarListener)
    }

}
