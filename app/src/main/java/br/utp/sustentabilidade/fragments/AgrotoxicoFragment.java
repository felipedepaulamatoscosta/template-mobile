package br.utp.sustentabilidade.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.utp.sustentabilidade.R;
import br.utp.sustentabilidade.activities.CadastraAgrotoxico;
import br.utp.sustentabilidade.databinding.FragmentAgrotoxicoBinding;
import br.utp.sustentabilidade.models.Agrotoxico;
import br.utp.sustentabilidade.models.RespostaJSON;
import br.utp.sustentabilidade.network.NetworkManager;
import br.utp.sustentabilidade.widgets.adapters.AgrotoxicoAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgrotoxicoFragment extends Fragment implements AgrotoxicoAdapter.AgrotoxicoListener {

    private FragmentAgrotoxicoBinding mBinding;
    private List<Agrotoxico> mAgrotoxico;
    private int mProximaPagina = 0;
    private AgrotoxicoAdapter mAdapter;
    private boolean isDestroying;
    /**
     * Construtor de fragmentos.
     *
     * @return Retorna uma instância do fragmento de agrotoxicos
     */
    public static AgrotoxicoFragment newInstance() {
        return new AgrotoxicoFragment();
    }

    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_agrotoxico, container, false);

        // Inicializa a lista de agrotoxicos
        mAgrotoxico = new ArrayList<>();


        // Inicializa o recycler view
        mAdapter = new AgrotoxicoAdapter(mAgrotoxico, this);
        LinearLayoutManager layout = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        mBinding.agrotoxicoRecyclerView.setAdapter(mAdapter);
        mBinding.agrotoxicoRecyclerView.setLayoutManager(layout);

        // Exibe a progressbar
        mBinding.agrotoxicoLoading.setVisibility(View.VISIBLE);

        mBinding.agrotoxicoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();

                boolean endHasBeenReached = lastVisible + 5 >= totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached) {
                    if (mProximaPagina >= 0) {
                        mProximaPagina++;
                        carregarWebService(mProximaPagina);
                    }
                }
            }
        });

        btnListener();


        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onResume() {
        isDestroying = false;
        mProximaPagina = 0;
        mAgrotoxico.clear();
        carregarWebService(mProximaPagina);
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroying = true;
        NetworkManager.cancelRequests();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void carregarWebService(final int pagina) {

        if (pagina < 0) return;

        Call<RespostaJSON<List<Agrotoxico>>> call = NetworkManager.service().listarAgrotoxicos(pagina);
        call.enqueue(new Callback<RespostaJSON<List<Agrotoxico>>>() {

            @Override
            public void onResponse(final Call<RespostaJSON<List<Agrotoxico>>> call, final Response<RespostaJSON<List<Agrotoxico>>> response) {

                RespostaJSON<List<Agrotoxico>> resposta = response.body();
                Log.d("TAG", "onResponse: " + resposta);
                Log.d("TAG", "onResponse: " + resposta.getStatus());
                if (resposta != null && resposta.getStatus() == 0) {
                    atualizarListaAgrotoxico(resposta.getObject());
                } else {
                    exibirMensagemErro();
                }
            }

            @Override
            public void onFailure(final Call<RespostaJSON<List<Agrotoxico>>> call, final Throwable t) {
                Log.e("TAG", "onFailure: ", t);
                exibirMensagemErro();
            }
        });
    }


    private void atualizarListaAgrotoxico(final List<Agrotoxico> agrotoxicos) {

        mBinding.agrotoxicoLoading.setVisibility(View.GONE);
        if (agrotoxicos.isEmpty()) {
            mProximaPagina = -1;
        } else {
            mProximaPagina += 1;             // Atualiza a proxima pagina

            mAgrotoxico.addAll(agrotoxicos);    // Adiciona todos elementos na lista
            mAdapter.notifyDataSetChanged(); // Refresca a tela
        }
    }

    private void exibirMensagemErro() {

        // Esconde a progressbar
        mBinding.agrotoxicoLoading.setVisibility(View.GONE);

        if (isDestroying)
            return;
            // Exibe um snackbar com a mensagem de erro
        Snackbar.make(mBinding.getRoot(), R.string.erro_webservice, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onAgrotoxicoClick(final Agrotoxico agrotoxico) {

        Gson gson = new Gson();
        String json = gson.toJson(agrotoxico);
        Intent intent = new Intent(getContext(), CadastraAgrotoxico.class);
        intent.putExtra("agrotoxico", json);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onFotoClick(final Agrotoxico agrotoxico) {
        return;
    }


    @Override
    public void onAgrotoxicoEditarClick(Agrotoxico agrotoxico) {
        Gson gson = new Gson();
        String json = gson.toJson(agrotoxico);
        Intent intent = new Intent(getContext(), CadastraAgrotoxico.class);
        intent.putExtra("agrotoxico", json);
        startActivity(intent);
        getActivity().finish();
    }


    @Override
    public void onAgrotoxicoDeletarClick(final Agrotoxico agrotoxico) {

        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Deletar Agrotóxico")
                .setMessage("Você tem certeza que deseja deletar o agrotóxico: " + agrotoxico.getTitulo() + "?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<RespostaJSON<Agrotoxico>> call = NetworkManager.service().removerAgrotoxico(Integer.valueOf(agrotoxico.getId()));
                        call.enqueue(new Callback<RespostaJSON<Agrotoxico>>() {
                            @Override
                            public void onResponse(Call<RespostaJSON<Agrotoxico>> call, Response<RespostaJSON<Agrotoxico>> response) {

                                RespostaJSON<Agrotoxico> resposta = response.body();
                                Log.d("TAG", "onResponse: " + resposta);
                                Log.d("TAG", "onResponse: " + resposta.getStatus());

                                if (resposta != null && resposta.getStatus() == 0) {
                                    mAgrotoxico.remove(agrotoxico);
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    exibirMensagemErro();
                                }
                            }

                            @Override
                            public void onFailure(Call<RespostaJSON<Agrotoxico>> call, Throwable t) {

                                Toast.makeText(getContext(), "Erro deletar!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                })
                .setNegativeButton("Não", null)
                .show();

    }


    void btnListener() {
        mBinding.agrotoxicoFloatAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CadastraAgrotoxico.class);
                startActivity(intent);
            }
        });
    }

}


