package com.paypay.currency.ui.adapters.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * Item Decoration for recyclerview adapter, to use with Grid Layout
 * @param columnsNumber number of columns
 * @param spacingSize space between items, in pixel
 * @param includeEdge include the edges for decoration
 */
class GridSpacerDecoration(
    private val columnsNumber: Int,
    private val spacingSize: Int,
    private val includeEdge: Boolean
) :
    ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % columnsNumber // item column
        if (includeEdge) {
            outRect.left =
                spacingSize - column * spacingSize / columnsNumber // spacing - column * ((1f / spanCount) * spacing)
            outRect.right =
                (column + 1) * spacingSize / columnsNumber // (column + 1) * ((1f / spanCount) * spacing)
            if (position < columnsNumber) { // top edge
                outRect.top = spacingSize
            }
            outRect.bottom = spacingSize // item bottom
        } else {
            outRect.left = column * spacingSize / columnsNumber // column * ((1f / spanCount) * spacing)
            outRect.right =
                spacingSize - (column + 1) * spacingSize / columnsNumber // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= columnsNumber) {
                outRect.top = spacingSize // item top
            }
        }
    }
}