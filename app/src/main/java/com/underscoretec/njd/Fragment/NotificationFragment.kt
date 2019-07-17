package com.underscoretec.njd.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.underscoretec.njd.Adapters.CustomAdapter
import com.underscoretec.njd.Adapters.Notification_adapter
import com.underscoretec.njd.Models.Notification
import com.underscoretec.njd.R
import kotlinx.android.synthetic.*


// TODO: Rename parameter arguments, choose names that match

class NotificationFragment : Fragment() {

    lateinit var recycler_notification: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_notification, container, false)


        recycler_notification = view.findViewById(R.id.recycler_notification)

        recycler_notification.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        val notify = ArrayList<Notification>()

        notify.add(Notification(R.drawable.notify_1, "Remember you have to credit the author", "2 min ago"))
        notify.add(Notification(R.drawable.notify_2, "Remember you have to credit the author", "2 min ago"))
        notify.add(Notification(R.drawable.notify_1, "Remember you have to credit the author", "2 min ago"))
        notify.add(Notification(R.drawable.notify_2, "Remember you have to credit the author", "2 min ago"))
        notify.add(Notification(R.drawable.notify_1, "Remember you have to credit the author", "2 min ago"))
        notify.add(Notification(R.drawable.notify_2, "Remember you have to credit the author", "2 min ago"))
        notify.add(Notification(R.drawable.notify_1, "Remember you have to credit the author", "2 min ago"))
        notify.add(Notification(R.drawable.notify_2, "Remember you have to credit the author", "2 min ago"))
        notify.add(Notification(R.drawable.notify_1, "Remember you have to credit the author", "2 min ago"))
        notify.add(Notification(R.drawable.notify_2, "Remember you have to credit the author", "2 min ago"))


        val adapter = Notification_adapter(notify)

        recycler_notification.adapter = adapter

        return view
    }


}
