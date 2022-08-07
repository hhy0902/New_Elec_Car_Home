package org.techtown.new_elec_car_home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.techtown.new_elec_car_home.model.Data

class CarListAdapter(val buttonClicked : (Data) -> Unit ) : ListAdapter<Data, CarListAdapter.ItemViewHolder>(differ)  {


    inner class ItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bind(data: Data) {
            val address = itemView.findViewById<TextView>(R.id.addressTextView)
            val searchButton = itemView.findViewById<Button>(R.id.searchButton)
            //val name = itemView.findViewById<TextView>(R.id.nameTextView)
            val chargeTP = itemView.findViewById<TextView>(R.id.ChargeTP)
            val cpTp = itemView.findViewById<TextView>(R.id.cpTp)
            val cpStat = itemView.findViewById<TextView>(R.id.cpStat)

            address.text = data.addr + " / " + data.csNm
            //name.text = data.csNm

            when(data.cpStat) {
                "1" -> {cpStat.text = "충전기 상태 : 충전가능"}
                "2" -> {cpStat.text = "충전기 상태 : 충전중"}
                "3" -> {cpStat.text = "충전기 상태 : 고장/점검"}
                "4" -> {cpStat.text = "충전기 상태 : 통신장애"}
                "5" -> {cpStat.text = "충전기 상태 : 통신미연결"}
            }
            when(data.chargeTp) {
                "1" -> {chargeTP.text = "충전기타입 : 완속"}
                "2" -> {chargeTP.text = "충전기타입 : 급속"}
            }
            when(data.cpTp) {
                "1" -> {cpTp.text = "충전방식 : B타입(5핀)"}
                "2" -> {cpTp.text = "충전방식 : C타입(5핀)"}
                "3" -> {cpTp.text = "충전방식 : BC타입(5핀)"}
                "4" -> {cpTp.text = "충전방식 : BC타입(7핀)"}
                "5" -> {cpTp.text = "충전방식 : DC차 데모"}
                "6" -> {cpTp.text = "충전방식 : AC 3상"}
                "7" -> {cpTp.text = "충전방식 : DC콤보"}
                "8" -> {cpTp.text = "충전방식 : DC차데모+DC콤보"}
                "9" -> {cpTp.text = "충전방식 : DC차데모+AC3상"}
                "10" -> {cpTp.text = "충전방식 : DC차데모+DC콤보, AC3상"}
            }


            searchButton.setOnClickListener {
                buttonClicked(data)
                //Toast.makeText(itemView.context, "${data.csNm}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.item_station,parent,false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList.get(position))
    }

    companion object {
        val differ = object : DiffUtil.ItemCallback<Data>() {
            override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
                return oldItem.addr == newItem.addr
            }

            override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
                return oldItem == newItem
            }

        }
    }

}









































