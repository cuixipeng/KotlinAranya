package com.aranya.kotlinaranya.ui.fragments.home.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.SyncStateContract
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import cn.bingoogolapple.bgabanner.BGABanner
import com.aranya.kotlinaranya.R
import com.aranya.kotlinaranya.bean.HomeBean
import com.aranya.kotlinaranya.ui.activity.playdetail.VideoDetailActivity
import com.basic.library.durationFormat
import com.basic.library.view.recyclerview.ViewHolder
import com.basic.library.view.recyclerview.adapter.CommonAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.reactivex.Observable

/**
 * @author cuixipeng
 * @date 2019-08-29.
 * @description
 */
class HomeAdapter(context: Context, data: ArrayList<HomeBean.Issue.Item>) :
    CommonAdapter<HomeBean.Issue.Item>(context, data, -1) {
    var bannerItemSize = 0

    companion object {
        private const val ITEM_TYPE_BANNER = 1//banner类型
        private const val ITEM_TYPE_TEXT_HEADER = 2//textheader
        private const val ITEM_TYPE_CONTENT = 3//item
    }

    /**
     * 设置banner大小
     */
    fun setBannerSize(count: Int) {
        bannerItemSize = count
    }

    /**
     * 添加更多数据
     */
    fun addItemData(itemList: ArrayList<HomeBean.Issue.Item>) {
        this.mData.addAll(itemList)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> ITEM_TYPE_BANNER
            mData[position + bannerItemSize - 1].type == "textHeader" ->
                ITEM_TYPE_TEXT_HEADER
            else -> ITEM_TYPE_CONTENT
        }
    }

    override fun getItemCount(): Int {
        return when {
            mData.size > bannerItemSize -> mData.size - bannerItemSize + 1
            mData.isEmpty() -> 0
            else -> 1
        }
    }

    /**
     * 绑定布局
     */
    override fun bindData(holder: ViewHolder, data: HomeBean.Issue.Item?, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE_BANNER -> {
                val bannerItemData: ArrayList<HomeBean.Issue.Item> =
                    mData.take(bannerItemSize).toCollection(ArrayList())
                val bannerFeedList = ArrayList<String>()
                val bannerTitleList = ArrayList<String>()
                //取出banner 显示的img和title
                Observable.fromIterable(bannerItemData).subscribe { list ->
                    bannerFeedList.add(list.data?.cover?.feed ?: "")
                    bannerTitleList.add(list.data?.title ?: "")
                }
                //设置banner
                with(holder) {
                    getView<BGABanner>(R.id.banner).run {
                        setAutoPlayAble(bannerFeedList.size > 1)
                        setData(bannerFeedList, bannerTitleList)
                        setAdapter { banner, _, feedImagurl, position ->
                            Glide.with(mContext)
                                .load(feedImagurl)
                                .transition(DrawableTransitionOptions().crossFade())
                                .into(banner.getItemImageView(position))
                        }
                    }
                }
                //没有使用到的参数在kotlin中用"_"代替
                holder.getView<BGABanner>(R.id.banner).setDelegate { _, imageView, _, i ->
                    goToVideoPlayer(mContext as Activity, imageView, bannerItemData[i])
                }
            }
            ITEM_TYPE_TEXT_HEADER
            -> {
                holder.setText(R.id.tvHeader, mData[position + bannerItemSize - 1].data?.text ?: "")
            }
            ITEM_TYPE_CONTENT
            -> {
                setVideoItem(holder, mData[position + bannerItemSize - 1])
            }


        }
    }

    /**
     *  创建布局
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ITEM_TYPE_BANNER ->
                ViewHolder(inflaterView(R.layout.item_home_banner, parent))
            ITEM_TYPE_TEXT_HEADER ->
                ViewHolder(inflaterView(R.layout.item_home_header, parent))
            else ->
                ViewHolder(inflaterView(R.layout.item_home_content, parent))
        }
    }

    /**
     * 加载布局
     */
    private fun inflaterView(mLayoutId: Int, parent: ViewGroup): View {
        //创建view
        val view = mInflater?.inflate(mLayoutId, parent, false)
        return view ?: View(parent.context)
    }


    /**
     * 加载 content item
     */
    private fun setVideoItem(holder: ViewHolder, item: HomeBean.Issue.Item) {
        val itemData = item.data

        val defAvatar = R.mipmap.default_avatar
        val cover = itemData?.cover?.feed
        var avatar = itemData?.author?.icon
        var tagText: String? = "#"

        // 作者出处为空，就显获取提供者的信息
        if (avatar.isNullOrEmpty()) {
            avatar = itemData?.provider?.icon
        }
        // 加载封页图
        Glide.with(mContext)
            .load(cover)
            .placeholder(R.mipmap.default_image)
            .transition(DrawableTransitionOptions().crossFade())
            .into(holder.getView(R.id.iv_cover_feed))

        // 如果提供者信息为空，就显示默认
        if (avatar.isNullOrEmpty()) {
            Glide.with(mContext)
                .load(defAvatar)
                .placeholder(R.mipmap.default_avatar).circleCrop()
                .transition(DrawableTransitionOptions().crossFade())
                .into(holder.getView(R.id.iv_avatar))

        } else {
            Glide.with(mContext)
                .load(avatar)
                .placeholder(R.mipmap.default_avatar).circleCrop()
                .transition(DrawableTransitionOptions().crossFade())
                .into(holder.getView(R.id.iv_avatar))
        }
        holder.setText(R.id.tv_title, itemData?.title ?: "")

        //遍历标签
        itemData?.tags?.take(4)?.forEach {
            tagText += (it.name + "/")
        }
        // 格式化时间
        val timeFormat = durationFormat(itemData?.duration)

        tagText += timeFormat

        holder.setText(R.id.tv_tag, tagText!!)

        holder.setText(R.id.tv_category, "#" + itemData?.category)

        holder.setOnItemClickListener(listener = View.OnClickListener {
            goToVideoPlayer(mContext as Activity, holder.getView(R.id.iv_cover_feed), item)
        })
    }

    /**
     * 跳转到视频详情页面播放
     *
     * @param activity
     * @param view
     */
    private fun goToVideoPlayer(activity: Activity, view: View, itemData: HomeBean.Issue.Item) {
        val intent = Intent(activity, VideoDetailActivity::class.java)
//        intent.putExtra(SyncStateContract.Constants.BUNDLE_VIDEO_DATA, itemData)
        intent.putExtra(VideoDetailActivity.TRANSITION, true)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val pair = Pair(view, VideoDetailActivity.IMG_TRANSITION)
            val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pair)
            ActivityCompat.startActivity(activity, intent, activityOptions.toBundle())

        } else {
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
        }
    }


}