package br.utp.sustentabilidade.widgets.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.utp.sustentabilidade.R;
import br.utp.sustentabilidade.databinding.ItemAgrotoxicoBinding;
import br.utp.sustentabilidade.models.Agrotoxico;

public class AgrotoxicoAdapter extends RecyclerView.Adapter<AgrotoxicoAdapter.AgrotoxicoViewHolder> {

    private final List<Agrotoxico> mAgrotoxicos;
    private final AgrotoxicoListener mListener;

    public AgrotoxicoAdapter(List<Agrotoxico> agrotoxicos, AgrotoxicoListener listener) {
        mAgrotoxicos = agrotoxicos;
        mListener = listener;
    }

    @NonNull
    @Override
    public AgrotoxicoViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemAgrotoxicoBinding binding = ItemAgrotoxicoBinding.inflate(layoutInflater, parent, false);
        return new AgrotoxicoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final AgrotoxicoViewHolder holder, final int position) {
        holder.bind(mAgrotoxicos.get(position));
    }

    @Override
    public int getItemCount() {
        return mAgrotoxicos.size();
    }

    /**
     * Eventos de callback do adapter.
     */
    public interface AgrotoxicoListener {

        void onFotoClick(Agrotoxico agrotoxico);
        void onAgrotoxicoClick(Agrotoxico organico);
        void onAgrotoxicoDeletarClick(Agrotoxico organico);
        void onAgrotoxicoEditarClick(Agrotoxico organico);
    }

    /**
     * Armazena os dados da view.
     */
    class AgrotoxicoViewHolder extends RecyclerView.ViewHolder {

        private final ItemAgrotoxicoBinding mBinding;

        public AgrotoxicoViewHolder(final ItemAgrotoxicoBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(final Agrotoxico agrotoxico) {
            mBinding.setAgrotoxico(agrotoxico);

            // Exibir foto
            Glide.with(mBinding.agrotoxicoImgFoto.getContext())
                    .load(agrotoxico.getFoto())
                    .error(R.drawable.ic_placeholder)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(mBinding.agrotoxicoImgFoto);

            // Amarrar eventos
            mBinding.agrotoxicoImgFoto.setOnClickListener(v -> mListener.onFotoClick(agrotoxico));
            mBinding.agrotoxicoCard.setOnClickListener(v -> mListener.onAgrotoxicoClick(agrotoxico));
            mBinding.btExclui.setOnClickListener(v -> mListener.onAgrotoxicoDeletarClick(agrotoxico));
            mBinding.btEdita.setOnClickListener(v -> mListener.onAgrotoxicoEditarClick(agrotoxico));


        }
    }
}
