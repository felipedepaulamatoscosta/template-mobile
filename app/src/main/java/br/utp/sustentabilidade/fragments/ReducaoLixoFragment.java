package br.utp.sustentabilidade.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import br.utp.sustentabilidade.activities.CadastraReducao;
import br.utp.sustentabilidade.databinding.FragmentReducaoLixoBinding;
import br.utp.sustentabilidade.models.ReducaoLixo;
import br.utp.sustentabilidade.models.RespostaJSON;
import br.utp.sustentabilidade.network.NetworkManager;
import br.utp.sustentabilidade.widgets.adapters.ReducaoLixoAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReducaoLixoFragment extends Fragment implements ReducaoLixoAdapter.ReducaoLixoListener {

    private FragmentReducaoLixoBinding mBinding;
    private List<ReducaoLixo> mReducaoLixo;
    private int mProximaPagina = 0;
    private ReducaoLixoAdapter mAdapter;
    private boolean isDestroying;
    /**
     * Construtor de fragmentos.
     *
     * @return Retorna uma instância do fragmento de reducaolixo
     */
    public static ReducaoLixoFragment newInstance() {
        return new ReducaoLixoFragment();
    }

    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reducao_lixo, container, false);

        // Inicializa a lista de reducao
        mReducaoLixo = new ArrayList<>();


        // Inicializa o recycler view
        mAdapter = new ReducaoLixoAdapter(mReducaoLixo, this);
        LinearLayoutManager layout = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        mBinding.reducaoLixoRecyclerView.setAdapter(mAdapter);
        mBinding.reducaoLixoRecyclerView.setLayoutManager(layout);

        // Exibe a progressbar
        mBinding.reducaoLixoLoading.setVisibility(View.VISIBLE);

        mBinding.reducaoLixoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        mReducaoLixo.clear();
        mProximaPagina = 0;
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

        Call<RespostaJSON<List<ReducaoLixo>>> call = NetworkManager.service().listarReducaoLixos(pagina);
        call.enqueue(new Callback<RespostaJSON<List<ReducaoLixo>>>() {

            @Override
            public void onResponse(final Call<RespostaJSON<List<ReducaoLixo>>> call, final Response<RespostaJSON<List<ReducaoLixo>>> response) {

                RespostaJSON<List<ReducaoLixo>> resposta = response.body();
                Log.d("TAG", "onResponse: " + resposta);
                Log.d("TAG", "onResponse: " + resposta.getStatus());
                if (resposta != null && resposta.getStatus() == 0) {
                    atualizarListaReducaoLixo(resposta.getObject());
                } else {
                    exibirMensagemErro();
                }
            }

            @Override
            public void onFailure(final Call<RespostaJSON<List<ReducaoLixo>>> call, final Throwable t) {
                Log.e("TAG", "onFailure: ", t);
                exibirMensagemErro();
            }
        });
    }


    private void atualizarListaReducaoLixo(final List<ReducaoLixo> reducaoLixos) {


        // Atualiza os elementos da lista
        if (reducaoLixos.isEmpty()) {
            mProximaPagina = -1;
        } else {

            mReducaoLixo.addAll(reducaoLixos);
            mBinding.reducaoLixoRecyclerView.getAdapter().notifyDataSetChanged();

            mBinding.reducaoLixoLoading.setVisibility(View.GONE);
        }
    }

    private void exibirMensagemErro() {
        // Esconde a progressbar
        mBinding.reducaoLixoLoading.setVisibility(View.GONE);
        if (isDestroying)
            return;
        // Exibe um snackbar com a mensagem de erro
        Snackbar.make(mBinding.getRoot(), R.string.erro_webservice, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onReducaoLixoClick(final ReducaoLixo reducaoLixo) {

        Gson gson = new Gson();
        String json = gson.toJson(reducaoLixo);
        Intent intent = new Intent(getContext(), CadastraReducao.class);
        intent.putExtra("reducaoLixo", json);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onFotoClick(final ReducaoLixo reducaoLixo) {
        return;
    }


    @Override
    public void onReducaoLixoEditarClick(ReducaoLixo reducaoLixo) {
        Gson gson = new Gson();
        String json = gson.toJson(reducaoLixo);
        Intent intent = new Intent(getContext(), CadastraReducao.class);
        intent.putExtra("reducaoLixo", json);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onReducaoLixoDeletarClick(final ReducaoLixo reducaoLixo) {

        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Deletar Redução de Lixo")
                .setMessage("Você tem certeza que deseja deletar a forma redução de lixo: " + reducaoLixo.getTitulo() + "?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<RespostaJSON<ReducaoLixo>> call = NetworkManager.service().removerReducaoLixo(Integer.valueOf(reducaoLixo.getId()));
                        call.enqueue(new Callback<RespostaJSON<ReducaoLixo>>() {
                            @Override
                            public void onResponse(Call<RespostaJSON<ReducaoLixo>> call, Response<RespostaJSON<ReducaoLixo>> response) {

                                RespostaJSON<ReducaoLixo> resposta = response.body();
                                Log.d("TAG", "onResponse: " + resposta);
                                Log.d("TAG", "onResponse: " + resposta.getStatus());

                                if (resposta != null && resposta.getStatus() == 0) {
                                    mReducaoLixo.remove(reducaoLixo);
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    exibirMensagemErro();
                                }
                            }

                            @Override
                            public void onFailure(Call<RespostaJSON<ReducaoLixo>> call, Throwable t) {

                                Toast.makeText(getContext(), "Erro deletar!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                })
                .setNegativeButton("Não", null)
                .show();

    }


    void btnListener() {
        mBinding.reducaoLixoFloatAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CadastraReducao.class);
                startActivity(intent);
            }
        });
    }

}


