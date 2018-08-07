package ml.a0x00000000.mjavascript

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class RecyclerViewSpacesItemDecoration(val spaces: Spaces?): RecyclerView.ItemDecoration() {
    interface Spaces {
        var top: Int
        var bottom: Int
        var left: Int
        var right: Int
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.top = spaces?.top?:outRect.top
        outRect.bottom = spaces?.bottom?:outRect.bottom
        outRect.left = spaces?.left?:outRect.left
        outRect.right = spaces?.right?:outRect.right
        if(parent.getChildAdapterPosition(view) == 0) {
            outRect.top = spaces?.top?:outRect.top + view.resources.getDimensionPixelSize(R.dimen.card_margin)
        }
    }
}

//        val cardMargin: Int = resources.getDimensionPixelSize(R.dimen.card_margin)
//        recyclerView.addItemDecoration(RecyclerViewSpacesItemDecoration(object: RecyclerViewSpacesItemDecoration.Spaces {
//            override var top = cardMargin / 2
//            override var bottom = cardMargin / 2
//            override var left = cardMargin
//            override var right = cardMargin
//        }))