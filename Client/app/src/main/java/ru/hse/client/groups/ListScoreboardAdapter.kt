package ru.hse.client.groups

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import ru.hse.client.R

class ListScoreboardAdapter(context: Context, dataArrayList: ArrayList<ListScoreboardData?>?) :
    ArrayAdapter<ListScoreboardData?>(context, R.layout.scoreboard_list_item, dataArrayList!!) {
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        val listData = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.scoreboard_list_item, parent, false)
        }
        val memberName = view!!.findViewById<TextView>(R.id.user_name)
        val solvedTests = view.findViewById<TextView>(R.id.solved_tests)
        val avgScore = view.findViewById<TextView>(R.id.avg_score)
        val star = view.findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.star)

        memberName!!.text = listData!!.name
        memberName.ellipsize = TextUtils.TruncateAt.END
        memberName.maxLines = 1

        solvedTests!!.text = "Solved " + listData.testsSolved.toString() + " tests"
        avgScore.text = listData.avgScore.toString() + "%"
        if (listData.pos == 0) {
            star.setImageResource(R.drawable.gold_star)
        } else if (listData.pos == 1) {
            star.setImageResource(R.drawable.silver_star)
        } else if (listData.pos == 2) {
            star.setImageResource(R.drawable.bronze_star)
        }
        return view
    }
}