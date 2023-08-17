package com.mio.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mio.base.databinding.Fragment3Binding;
import com.mio.base.databinding.ItemRvLeftTest3Binding;
import com.mio.basic.BaseFragment;
import com.mio.basic.BaseMultiRecyclerViewAdapter;
import com.mio.basic.BaseRecyclerViewAdapter;
import com.mio.utils.TestUtils;

import java.util.ArrayList;

public class Test3Fragment extends BaseFragment<Fragment3Binding> {

    private BaseRecyclerViewAdapter<ItemRvLeftTest3Binding, String> leftAdapter;
    private BaseMultiRecyclerViewAdapter<String> rightAdapter;

    @Override
    protected void initView() {
        initLeft();
        initRight();
    }

    private void initLeft() {
        mDataBinding.rvLeft.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        leftAdapter = new BaseRecyclerViewAdapter<ItemRvLeftTest3Binding, String>
                (R.layout.item_rv_left_test3) {
            @Override
            protected void bind(ItemRvLeftTest3Binding binding, String bean, int position) {
                binding.tvContent.setText(bean);
            }
        };
        mDataBinding.rvLeft.setAdapter(leftAdapter);
        leftAdapter.setData(TestUtils.getTestStringList(20));
    }

    private void initRight() {
        mDataBinding.rvRight.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        ArrayList<BaseMultiRecyclerViewAdapter.Item> items = new ArrayList<>();
        items.add(new BaseMultiRecyclerViewAdapter.Item(R.layout.item_rv_left_test3, 0));
        items.add(new BaseMultiRecyclerViewAdapter.Item(R.layout.item_rv_left_test3_rotation, 0));

        rightAdapter = new BaseMultiRecyclerViewAdapter<String>(items) {
            @Override
            protected void bind(View itemView, String bean, int position) {
                TextView textView = itemView.findViewById(R.id.tv_content);
                textView.setText(bean);
            }
        };
        rightAdapter.setData(TestUtils.getTestStringList(20));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_3;
    }
}
