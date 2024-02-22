package com.s19.spotbuy.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.s19.spotbuy.DataBase.ImageManager;
import com.s19.spotbuy.Models.ImageModel;
import com.s19.spotbuy.Others.DownloadImage;
import com.s19.spotbuy.R;

import java.util.List;

public class ScrollingImageAdapter extends PagerAdapter {
    private final Context context;
    private final List<String> images;
    private  final ImageManager imageManager;

    public ScrollingImageAdapter(Context context,List<String> images)
    {
        this.context=context;
        this.images=images;
        imageManager = new ImageManager(context);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return  view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
        @SuppressLint("InflateParams")
        View view = layoutInflater.inflate(R.layout.item_view_image_item,null);
        ImageView imageView=view.findViewById(R.id.image_view);
        ProgressBar loadingIndicator = view.findViewById(R.id.loadingIndicator);

       try{
           ImageModel imageModel =imageManager.getImageByLink(images.get(position));
           if(imageModel.getImageBitmap()!=null){
               imageView.setImageBitmap(imageModel.getImageBitmap());
           }
           else{
               new DownloadImage(imageView,loadingIndicator).execute(images.get(position));
           }
       }
       catch (Exception e){
           new DownloadImage(imageView,loadingIndicator).execute(images.get(position));
       }

        ViewPager viewPager=(ViewPager) container;
        viewPager.addView(view,0);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });


        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager=(ViewPager) container;
        View view=(View) object;
        viewPager.removeView(view);
    }

}
