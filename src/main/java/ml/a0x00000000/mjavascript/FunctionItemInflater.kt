package ml.a0x00000000.mjavascript

import android.content.Context
import android.content.res.XmlResourceParser
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class FunctionItemInflater constructor(val context: Context) {
    companion object {
        private fun getFunctionItems(context: Context, functionRes: Int): Array<MainFunctionAdapter.FunctionItem> {
            val xml: XmlResourceParser = context.resources.getXml(functionRes)
            val list: MutableList<MainFunctionAdapter.FunctionItem> = mutableListOf()
            while(xml.eventType != XmlResourceParser.END_DOCUMENT) {
                if(xml.eventType == XmlResourceParser.START_TAG) {
                    if(xml.name == "function") {
                        val id: String = xml.getAttributeValue(null, "id")
                        val icon: Int = xml.getAttributeResourceValue(null, "icon", 0)
                        val title: String = context.getString(xml.getAttributeResourceValue(null, "title", 0))
                        val content: String = context.getString(xml.getAttributeResourceValue(null, "content", 0))
                        list.add(object: MainFunctionAdapter.FunctionItem {
                            override var id = id as Any
                            override var icon = icon
                            override var title = title
                            override var content = content
                        })
                    }
                }
                xml.next()
            }
            return list.toTypedArray()
        }
    }
    lateinit var adapter: MainFunctionAdapter
    fun inflate(functionRes: Int, recyclerView: RecyclerView): FunctionItemInflater {
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = MainFunctionAdapter(context, getFunctionItems(context, functionRes))
        recyclerView.adapter = adapter
        return this
    }
}