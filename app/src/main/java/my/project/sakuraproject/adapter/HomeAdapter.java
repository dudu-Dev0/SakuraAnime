package my.project.sakuraproject.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import my.project.sakuraproject.R;
import my.project.sakuraproject.bean.HomeBean;
import my.project.sakuraproject.bean.HomeHeaderBean;

public class HomeAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;
    private Context context;
    private RecyclerView recyclerView;
    private HomeHeaderAdapter homeHeaderAdapter;
    private HomeItemAdapter homeItemAdapter;
    private OnItemClick onItemClick;

    public HomeAdapter(Context context, List data, OnItemClick onItemClick) {
        super(data);
        this.context = context;
        this.onItemClick = onItemClick;
        addItemType(TYPE_LEVEL_0, R.layout.item_home_header);
        addItemType(TYPE_LEVEL_1, R.layout.item_home);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case TYPE_LEVEL_0:
                HomeHeaderBean homeHeaderBean = (HomeHeaderBean) item;
                recyclerView = helper.getView(R.id.header_list);
                List<HomeHeaderBean.HeaderDataBean> headerDataBeans = homeHeaderBean.getData();
                recyclerView.setLayoutManager(new GridLayoutManager(context, headerDataBeans.size()));
                homeHeaderAdapter = new HomeHeaderAdapter(context, headerDataBeans);
                homeHeaderAdapter.setOnItemClickListener((adapter, view, position) -> {
                    onItemClick.onHeaderClick(headerDataBeans.get(position));
                });
                recyclerView.setAdapter(homeHeaderAdapter);
                break;
            case TYPE_LEVEL_1:
                HomeBean homeBean = (HomeBean) item;
                List<HomeBean.HomeItemBean> homeItemBean = homeBean.getData();
                helper.setText(R.id.title, homeBean.getTitle());
                helper.setTextColor(R.id.title, context.getResources().getColor(R.color.text_color_primary));
                helper.setBackgroundColor(R.id.root, context.getResources().getColor(R.color.window_bg));
                helper.setBackgroundColor(R.id.more, context.getResources().getColor(R.color.window_bg));
                if (homeBean.getMoreUrl().isEmpty())
                    helper.getView(R.id.img).setVisibility(View.GONE);
                else
                    helper.getView(R.id.img).setVisibility(View.VISIBLE);
                ImageView img = helper.getView(R.id.img);
                img.setColorFilter(context.getResources().getColor(R.color.text_color_primary));
                helper.addOnClickListener(R.id.more);
                recyclerView = helper.getView(R.id.rv_list);
                recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
                homeItemAdapter = new HomeItemAdapter(context, homeItemBean);
                homeItemAdapter.setOnItemClickListener((adapter, view, position) -> {
                    onItemClick.onAnimeClick(homeItemBean.get(position));
                });
                recyclerView.setPadding(0,0,0, 10);
                recyclerView.setAdapter(homeItemAdapter);
                break;
        }
    }

    public interface OnItemClick {
        void onHeaderClick(HomeHeaderBean.HeaderDataBean bean);
        void onAnimeClick(HomeBean.HomeItemBean data);
    }
}