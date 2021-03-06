package com.mit.tipcar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class notice_board_content extends AppCompatActivity implements android.view.View.OnClickListener {

    private static final String DEEP_LINK = "https://com.example.safecar.com/";
    final int GET_GALLERY_IMAGE = 200;
    private RecyclerView commnet_list;
    private ImageView iv_back, iv_favorite, iv_moveto_gallery,Content_image;
    private android.widget.TextView tv_content_title,tv_write,tv_list_content,tx_favorite,tx_no_comment_state,tv_date;
    private LinearLayout ln_favorite_button,ln_commnet_button,ln_share_button;
    private android.widget.ListView ln_content;
    private Button bt_comment_done;
    private EditText tx_comment_write;
    private Integer favoriteCount = 0;
    private String key = "";
    private int data = 0;
    private String title,content,title_user,visibility_count,favorite_count,write_day;
    private Bitmap image;
    private NestedScrollView scrollView;
    private ArrayList<RecyclerView_Dictionary> mArrayList;
    private RecyclerViewCustomAdapter mAdapter;
    private int count = -1;
    private Bitmap bitmap;
    private byte[] byteArray;
    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board_content);

        scrollView = findViewById(R.id.scrollView);
        commnet_list = findViewById(R.id.comment_list);
//        ln_content = findViewById(R.id.ln_content);
        iv_back = findViewById(R.id.iv_back);
        ln_favorite_button = findViewById(R.id.ln_favorite_button);
        ln_commnet_button = findViewById(R.id.ln_commnet_button);
        ln_share_button=findViewById(R.id.ln_share_button);
        tx_favorite = findViewById(R.id.tx_favorite);
        iv_favorite = findViewById(R.id.iv_favorite);
        tv_content_title = findViewById(R.id.tv_content_title);
        tv_write = findViewById(R.id.tv_write);
        tv_list_content = findViewById(R.id.tv_list_content);
        tv_date = findViewById(R.id.tv_date);
        iv_moveto_gallery = findViewById(R.id.iv_moveto_gallery);
        bt_comment_done = findViewById(R.id.bt_comment_done);
        tx_comment_write = findViewById(R.id.tx_comment_write);
        tx_no_comment_state = findViewById(R.id.tx_no_comment_state);
        Content_image = findViewById(R.id.Content_image);

        //TODO ?????? ?????? ????????? ?????????????????? ???????????? ?????? ?????? ??? ???????????? ?????? ???????????? ?????????????????? ????????? ???

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.comment_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mArrayList = new ArrayList<>();
        mAdapter = new RecyclerViewCustomAdapter( mArrayList);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);




        ln_favorite_button.setOnClickListener(this);
        ln_commnet_button.setOnClickListener(this);
        ln_share_button.setOnClickListener(this);
        iv_back.setOnClickListener((android.view.View.OnClickListener) this);
        iv_moveto_gallery.setOnClickListener(this);
        bt_comment_done.setOnClickListener(this);
        tx_comment_write.setOnClickListener(this);
        tx_comment_write.requestFocus();

        Intent intent = getIntent();
        data = intent.getExtras().getInt("position");   //????????? ?????????
        title = intent.getExtras().getString("title");  //??????
        content = intent.getExtras().getString("Content");  //??????
        title_user = intent.getExtras().getString("title_user");    //?????????
        visibility_count = intent.getExtras().getString("visibility_count");        //?????????
        favorite_count = intent.getExtras().getString("favorite_count");            //?????????
        write_day = intent.getExtras().getString("writeday");                       //??????
         byteArray = getIntent().getByteArrayExtra("image_content");
        if(byteArray!= null) {
             bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
        if(bitmap != null) {
            Content_image.setVisibility(android.view.View.VISIBLE);
            Content_image.setImageBitmap(bitmap);
            byteArray = null;
            bitmap = null;
        }else{
            Content_image.setVisibility(android.view.View.GONE);
        }
        tv_content_title.setText(title);
        tv_write.setText("?????????  "+title_user);
        tv_list_content.setText(content);
        tv_date.setText("????????????  "+write_day);




        //?????????
        Uri uri = getIntent().getData();
        if (uri != null) {
            java.util.List<String> params = uri.getPathSegments();
            String id = params.get(params.size()-1);
            Toast.makeText(this,"id="+id, Toast.LENGTH_SHORT).show();
        }
        //????????????
        key = String.valueOf(data);


        //favorite count ?????? ???????????? ????????????????????? ??? ????????????, ???????????? ????????? 0????????? ??????
        java.util.Set<String> findkey = Constants.FavoriteCountRemember.keySet();
        Iterator<String> findthis = findkey.iterator();
        while(findthis.hasNext()){
            if(TextUtils.equals(key,findthis.next())){
                favoriteCount = Constants.FavoriteCountRemember.get(key);
            }
        }
        if(favoriteCount ==1){
            favorite_count = String.valueOf(Integer.parseInt(favorite_count) +1);
            tx_favorite.setText(favorite_count);
        iv_favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
        }else if(favoriteCount == 0){
            tx_favorite.setVisibility(android.view.View.VISIBLE);
            iv_favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }

//
//        LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
//        ConstraintLayout linearLayout = (ConstraintLayout) inflater.inflate( R.layout.comment_list_item, null );
//        ln_content.addView(linearLayout);

//
//        MyAdapter adapter = new MyAdapter(this);
//
//
//        commnet_list.setAdapter(adapter);




            //TODO ?????? ?????? ????????? ?????????????????? ???????????? ?????? ?????? ??? ???????????? ?????? ???????????? ?????????????????? ????????? ???
//        if(item.getIcon() != null) {
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            Bitmap bitmap = ((BitmapDrawable) R.drawable.carimage).getBitmap();
//            float scale = (float) (1024 / (float) bitmap.getWidth());
//            int image_w = (int) (bitmap.getWidth() * scale);
//            int image_h = (int) (bitmap.getHeight() * scale);
//            Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
//            resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//            byteArray = stream.toByteArray();
//        }

        RecyclerView_Dictionary data = new RecyclerView_Dictionary("?????????", String.valueOf(R.drawable.carimage),"?????? ?????? ???????????? ?????????????????? ?????? ??????????????????!");
        RecyclerView_Dictionary data2 = new RecyclerView_Dictionary("?????????", String.valueOf(R.drawable.carimage),"?????? ?????? ???????????? ?????????????????? ?????? ??????????????????!");
        RecyclerView_Dictionary data3 = new RecyclerView_Dictionary("?????????", String.valueOf(R.drawable.carimage),"?????? ?????? ???????????? ?????????????????? ?????? ??????????????????!");
        RecyclerView_Dictionary data4 = new RecyclerView_Dictionary("?????????", String.valueOf(R.drawable.carimage),"?????? ?????? ???????????? ?????????????????? ?????? ??????????????????!");

        //mArrayList.add(0, dict); //RecyclerView??? ??? ?????? ??????
        mArrayList.add(data); // RecyclerView??? ????????? ?????? ??????
        mArrayList.add(data2);
        mArrayList.add(data3);
        mArrayList.add(data4);


        mAdapter.notifyDataSetChanged();

        //TODO ????????? ????????? ????????????
//        CommentListViewAdapter adapter;
//        adapter = new CommentListViewAdapter();
//        commnet_list.setAdapter(adapter);
//        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.carimage),
//                "User1", "????????????! ??? ???????????????????!") ;
//        // ??? ?????? ????????? ??????.
//        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.carimage),
//                "User2", "????????????!!! ??????????????? ????????? ?????? ?????? ??????????????????! ???????????????!!") ;
//        // ??? ?????? ????????? ??????.
//        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.carimage),
//                "User3", "????????? ?????????????????? ????????? ???????????????.. ") ;
//        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.carimage),
//                "User3", "????????? ?????????????????? ????????? ???????????????.. ") ;
//
//        //??????????????????
//        adapter.notifyDataSetChanged();
//       // setListViewHeightBasedOnChildren(commnet_list);
//
//        if(adapter.getCount() == 0){
//            //????????? ?????? ?????? ?????? ????????? ????????? ????????????. ??????.
//            tx_no_comment_state.setVisibility(View.VISIBLE);
//        }


    }

        //???????????? ?????????
        public void Create_DynamicLink(final String subject, String PageURL, String ImgUrl){
            Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse(PageURL))
                    .setDomainUriPrefix("????????????")
                    .setAndroidParameters(
                            new DynamicLink.AndroidParameters.Builder(getPackageName())
                                    .build())
                    .setSocialMetaTagParameters(
                            new DynamicLink.SocialMetaTagParameters.Builder()
                                    .setTitle("???????????? ???????????? ???????????????????")
                                    .setImageUrl(Uri.parse(ImgUrl))
                                    .build())
                    .buildShortDynamicLink()
                    .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<ShortDynamicLink> task) {
                            if (task.isSuccessful()) {
                                Uri ShortLink = task.getResult().getShortLink();
                                try {
                                    Intent Sharing_Intent = new Intent();
                                    Sharing_Intent.setAction(Intent.ACTION_SEND);
                                    Sharing_Intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                    Sharing_Intent.putExtra(Intent.EXTRA_TEXT, ShortLink.toString());
                                    Sharing_Intent.setType("text/plain");
                                    startActivity(Intent.createChooser(Sharing_Intent, "???????????? ????????????"));
                                }
                                catch (Exception e) {
                                }
                            }
                        }
                    });
        }


    public void onClick(android.view.View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ln_favorite_button:
                android.util.Log.d("KDH Like?","SUCCESS;");

                if (favoriteCount == 0) {
                    tx_favorite.setVisibility(android.view.View.GONE);
                    iv_favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                    favoriteCount = 1;
                    Constants.FavoriteCountRemember.put(String.valueOf(data),favoriteCount);

                } else if(favoriteCount == 1) {
                    tx_favorite.setVisibility(android.view.View.VISIBLE);
                    iv_favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    favoriteCount =0;
                    Constants.FavoriteCountRemember.put(String.valueOf(data),favoriteCount);
                }
                break;
            case R.id.ln_share_button:
                android.util.Log.d("KDH SHARE?","SUCCESS;");
                //1. ???????????? ????????? ??????, 2. ??? ?????? ?????? 3. ????????? uri
//                Create_DynamicLink("????????????",DEEP_LINK, String.valueOf(R.drawable.carimage));
                Intent Sharing_intent = new Intent(Intent.ACTION_SEND);
                Sharing_intent.setType("text/plain");

                String Test_Message = "????????? Text";

                Sharing_intent.putExtra(Intent.EXTRA_TEXT, Test_Message);

                Intent Sharing = Intent.createChooser(Sharing_intent, "????????????");
                startActivity(Sharing);

                break;
            case R.id.ln_commnet_button:
             //   tx_comment_write.performClick();
                InputMethodManager imm2 = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm2.showSoftInput(tx_comment_write, 0);
                break;

            case R.id.iv_moveto_gallery:
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent,GET_GALLERY_IMAGE);
            break;

            case R.id.bt_comment_done:
                //?????? ?????? ?????? = Today??? ??????
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                String Today = simpledate.format(date);

                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            iv_moveto_gallery.setImageURI(selectedImageUri);
            //tx_comment_write?????? ????????? ????????? ???????????? ????????? ???.

        }
    }

    //?????? ???????????? ?????? ??? ????????? ???????????? ?????????
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        android.view.View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

}