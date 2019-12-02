package br.utp.sustentabilidade.widgets.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.utp.sustentabilidade.R;
import br.utp.sustentabilidade.databinding.ItemReducaoLixoBinding;
import br.utp.sustentabilidade.models.ReducaoLixo;

public class ReducaoLixoAdapter extends RecyclerView.Adapter<ReducaoLixoAdapter.ReducaoLixoViewHolder> {

    private final List<ReducaoLixo> mReducaoLixos;
    private final ReducaoLixoListener mListener;

    public ReducaoLixoAdapter(List<ReducaoLixo> reducaoLixos, ReducaoLixoListener listener) {
        mReducaoLixos = reducaoLixos;
        mListener = listener;
    }

    @NonNull
    @Override
    public ReducaoLixoViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemReducaoLixoBinding binding = ItemReducaoLixoBinding.inflate(layoutInflater, parent, false);
        return new ReducaoLixoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReducaoLixoViewHolder holder, final int position) {
        holder.bind(mReducaoLixos.get(position));
    }

    @Override
    public int getItemCount() {
        return mReducaoLixos.size();
    }

    /**
     * Eventos de callback do adapter.
     */
    public interface ReducaoLixoListener {

        void onFotoClick(ReducaoLixo reducaoLixo);
        void onReducaoLixoClick(ReducaoLixo reducaoLixo);
        void onReducaoLixoDeletarClick(ReducaoLixo reducaoLixo);
        void onReducaoLixoEditarClick(ReducaoLixo reducaoLixo);
    }

    /**
     * Armazena os dados da view.
     */
    class ReducaoLixoViewHolder extends RecyclerView.ViewHolder {

        private final ItemReducaoLixoBinding mBinding;

        public ReducaoLixoViewHolder(final ItemReducaoLixoBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(final ReducaoLixo reducaoLixo) {
            mBinding.setReducaoLixo(reducaoLixo);

            // Exibir foto
            Glide.with(mBinding.reducaoLixoImgFoto.getContext())
                    .load(reducaoLixo.getFoto())
                    .error(R.drawable.ic_placeholder)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(mBinding.reducaoLixoImgFoto);

            // Amarrar eventos
            mBinding.reducaoLixoImgFoto.setOnClickListener(v -> mListener.onFotoClick(reducaoLixo));
            mBinding.reducaoLixoCard.setOnClickListener(v -> mListener.onReducaoLixoClick(reducaoLixo));
            mBinding.btExclui.setOnClickListener(v -> mListener.onReducaoLixoDeletarClick(reducaoLixo));
            mBinding.btEdita.setOnClickListener(v -> mListener.onReducaoLixoEditarClick(reducaoLixo));


        }
    }
}
