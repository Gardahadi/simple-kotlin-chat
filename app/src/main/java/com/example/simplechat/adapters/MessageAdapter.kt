package com.example.simplechat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechat.R
import com.example.simplechat.models.MessageModel
import com.google.android.material.card.MaterialCardView

// Recycler Adapter
    class MessageAdapter(private val messageList: ArrayList<MessageModel>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.chat_message_content, parent, false)
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