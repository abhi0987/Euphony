package music.abitri.com.euphony.AdapterPkg;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;

import music.abitri.com.euphony.R;
import music.abitri.com.euphony.SQLiteDataBasePackage.PlayListSQLdatabase;

/**
 * Created by abhis on 3/8/2017.
 */

public class Playlist_Db_adapter extends RecyclerView.Adapter<Playlist_Db_adapter.db_adapter> {
    Context context;
    PlayListSQLdatabase PDB;
    ArrayList<String> tb_names;

    public Playlist_Db_adapter(Context context,ArrayList<String> tb_names){
        this.context = context;
        this.tb_names = tb_names;
        PDB = new PlayListSQLdatabase(context);
    }
    @Override
    public Playlist_Db_adapter.db_adapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_playlist_name_view, parent, false);
        db_adapter holder = new db_adapter(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Playlist_Db_adapter.db_adapter holder, int position) {

        ViewGroup.LayoutParams lp = holder.layout.getLayoutParams();
        if (lp instanceof FlexboxLayoutManager.LayoutParams) {
            FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams)
                    holder.layout.getLayoutParams();
            flexboxLp.setFlexGrow(1.0f);
        }

        holder.names.setText(tb_names.get(position));

    }

    @Override
    public int getItemCount() {
        return tb_names.size();
    }


    public class db_adapter extends RecyclerView.ViewHolder{

        TextView names;
        RelativeLayout layout;


        public db_adapter(View itemView) {
            super(itemView);

            names = (TextView) itemView.findViewById(R.id.each_plst_nm);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout);
        }
    }
}
