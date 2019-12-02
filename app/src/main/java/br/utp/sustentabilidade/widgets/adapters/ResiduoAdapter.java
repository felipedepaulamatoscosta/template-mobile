package br.utp.sustentabilidade.widgets.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.utp.sustentabilidade.R;
import br.utp.sustentabilidade.databinding.ItemResiduoBinding;
import br.utp.sustentabilidade.models.Residuo;

public class ResiduoAdapter extends RecyclerView.Adapter<ResiduoAdapter.ResiduoViewHolder> {

    private final List<Residuo> mResiduos;
    private final ResiduoListener mListener;

    public ResiduoAdapter(List<Residuo> residuos, ResiduoListener listener) {
        mResiduos = residuos;
        mListener = listener;
    }

    @NonNull
    @Override
    public ResiduoViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemResiduoBinding binding = ItemResiduoBinding.inflate(layoutInflater, parent, false);
        return new ResiduoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ResiduoViewHolder holder, final int position) {
        holder.bind(mResiduos.get(position));
    }

    @Override
    public int getItemCount() {
        return mResiduos.size();
    }

    /**
     * Eventos de callback do adapter.
     */
    public interface ResiduoListener {

        void onFotoClick(Residuo residuo);
        void onResiduoClick(Residuo residuo);
        void onResiduoDeletarClick(Residuo residuo);
        void onResiduoEditarClick(Residuo residuo);
    }

    /**
     * Armazena os dados da view.
     */
    class ResiduoViewHolder extends RecyclerView.ViewHolder {

        private final ItemResiduoBinding mBinding;

        public ResiduoViewHolder(final ItemResiduoBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(final Residuo residuo) {
            mBinding.setResiduo(residuo);

            // Exibir foto
            Glide.with(mBinding.residuoImgFoto.getContext())
                    .load(residuo.getFoto())
                    .error(R.drawable.ic_placeholder)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(mBinding.residuoImgFoto);

            // Amarrar eventos
            mBinding.residuoImgFoto.setOnClickListener(v -> mListener.onFotoClick(residuo));
            mBinding.residuoCard.setOnClickListener(v -> mListener.onResiduoClick(residuo));
            mBinding.btExclui.setOnClickListener(v -> mListener.onResiduoDeletarClick(residuo));
            mBinding.btEdita.setOnClickListener(v -> mListener.onResiduoEditarClick(residuo));


        }
    }
}
