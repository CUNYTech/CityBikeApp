package com.cunycodes.bikearound;

import android.support.v4.app.Fragment;

public class CardFragment extends Fragment {
  /* String[] bikePaths = {"Central Park", "The High Line", "Fort Tyron", "Eastside River",
           "Hudson Walk", "Riverside Park"};

    int[] images = {R.mipmap.centralpark, R.mipmap.highlinr, R.mipmap.forttyron, R.mipmap.eastriver,
                    R.mipmap.hudson, R.mipmap.riverside};
    String[] address = {"Central Park New York, NY 10024", "The High Line New York, NY 10011","Fort Tyron Park Riverside Dr To Broadway, New York, NY 10040",
                        "John V. Lindsay East River Park East River Promenade, New York, NY 10002","Hudson River Greenway West Side Highway (Dyckman to Battery Park), New York, NY",
                        "Riverside Park New York, NY 10025"};
    RecyclerView mRecyclerView;
    ArrayList<PopularPaths> bikepath = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeList();
        getActivity().setTitle("Explore");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.card_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (bikepath.size() > 0 & mRecyclerView != null ){
            mRecyclerView.setAdapter(new BikePathAdapter(bikepath));
        }
        mRecyclerView.setLayoutManager(mLayoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public class BikePathViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView address;
        public ImageView image;

        public BikePathViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.titleTextView);
            address = (TextView) itemView.findViewById(R.id.addressTextView);
            image = (ImageView) itemView.findViewById(R.id.pathImageView);

            ///add onClickListener here
        }
    }

    public class BikePathAdapter extends RecyclerView.Adapter<BikePathViewHolder>{
        private ArrayList<PopularPaths> paths;

        public BikePathAdapter(ArrayList<PopularPaths> data){
            paths = data;
        }

        @Override
        public BikePathViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_cards, parent, false);
            BikePathViewHolder holder = new BikePathViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(BikePathViewHolder holder, int position) {
            holder.title.setText(paths.get(position).getCardName());
            holder.address.setText(paths.get(position).getAddress());
            holder.image.setImageResource(paths.get(position).getImageResourceId());
        }

        @Override
        public int getItemCount() {
            return paths.size();
        }
    }


    public void initializeList(){
        bikepath.clear();

        for (int i = 0; i<bikePaths.length; i++){
            PopularPaths path = new PopularPaths();
            path.setCardName(bikePaths[i]);
            path.setImageResourceId(images[i]);
            path.setAddress(address[i]);

            bikepath.add(path);
        }

    }
*/

}
