package com.dozuki.ifixit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.dozuki.ifixit.R;
import com.dozuki.ifixit.model.guide.GuideInfo;
import com.dozuki.ifixit.ui.guide.view.GuideViewActivity;
import com.dozuki.ifixit.util.APIEvent;
import com.dozuki.ifixit.util.APIService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class FavoritesActivity extends BaseActivity {
   private static final int LIMIT = 200;
   private int OFFSET = 0;
   private static final String GUIDES_KEY = "GUIDES_KEY";

   private ArrayList<GuideInfo> mGuides;
   private GridView mGridView;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setTitle(getString(R.string.favorites));

      setContentView(R.layout.favorites);

      if (savedInstanceState != null) {
         mGuides = (ArrayList<GuideInfo>)savedInstanceState.getSerializable(GUIDES_KEY);
         initGridView();
      } else {
         showLoading(R.id.favorites_loading);
         APIService.call(this, APIService.getUserFavorites(LIMIT, OFFSET));
      }
   }

   private void initGridView() {
      mGridView = (GridView) findViewById(R.id.guide_grid);
      mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> arg0, View view, int position,
          long id) {
            GuideInfo guide = mGuides.get(position);
            Intent intent = new Intent(FavoritesActivity.this, GuideViewActivity.class);

            intent.putExtra(GuideViewActivity.SAVED_GUIDEID, guide.mGuideid);
            startActivity(intent);
         }
      });

      mGridView.setAdapter(new GuideListAdapter(this, mGuides));
      mGridView.setEmptyView(findViewById(R.id.favorites_empty_view));
   }

   @Subscribe
   public void onGuides(APIEvent.UserFavorites event) {
      hideLoading();

      if (!event.hasError()) {
         if (mGuides != null) {
            mGuides.addAll(event.getResult());
         } else {
            mGuides = new ArrayList<GuideInfo>(event.getResult());
         }

         initGridView();
      } else {
         APIService.getErrorDialog(this, event.getError(), null).show();
      }
   }

   @Override
   public void onSaveInstanceState(Bundle state) {
      state.putSerializable(GUIDES_KEY, mGuides);
   }

   @Override
   public boolean finishActivityIfLoggedOut() {
      return true;
   }
}
