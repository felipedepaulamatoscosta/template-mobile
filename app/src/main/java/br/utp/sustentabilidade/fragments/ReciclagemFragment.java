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

import br.utp.sustentabilidade.activities.CadastraReciclagem;
import br.utp.sustentabilidade.databinding.FragmentReciclagemBinding;
import br.utp.sustentabilidade.models.Reciclagem;
import br.utp.sustentabilidade.models.RespostaJSON;
import br.utp.sustentabilidade.network.NetworkManager;
import br.utp.sustentabilidade.widgets.adapters.ReciclagemAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReciclagemFragment extends Fragment implements ReciclagemAdapter.ReciclagemListener {

    private FragmentReciclagemBinding mBinding;
    private List<Reciclagem> mReciclagem;
    private int mProximaPagina = 0;
    private ReciclagemAdapter mAdapter;
    private boolean isDestroying;
    /**
     * Construtor de fragmentos.
     *
     * @return Retorna uma instância do fragmento de reciclagens
     */
    public static ReciclagemFragment newInstance() {
        return new ReciclagemFragment();
    }

    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reciclagem, container, false);

        // Inicializa a lista de reciclagem
        mReciclagem = new ArrayList<>();


        // Inicializa o recycler view
        mAdapter = new ReciclagemAdapter(mReciclagem, this);
        LinearLayoutManager layout = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        mBinding.reciclagemRecyclerView.setAdapter(mAdapter);
        mBinding.reciclagemRecyclerView.setLayoutManager(layout);

        // Exibe a progressbar
        mBinding.reciclagemLoading.setVisibility(View.VISIBLE);

        // Inclusão do método de verificação da rolagem da lista para carregar a próxima pagina do service
        mBinding.reciclagemRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();

                boolean endHasBeenReached = lastVisible + 5 >= totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached) {
                    if(mProximaPagina >= 0) {
                        //caso a váriavel mProximapagina for igual a -1 (alteração no método atualizarListaAgrotoxico)
                        // não tentar carregar mais o serviço pois indica que não há mais registros
                        mProximaPagina++;
                        carregarWebService(mProximaPagina);
                    }
                }
            }
        });

        btnListener();
        // movido o método carregarWebService desse onCreateView para o onResume() para que
        // quando abrir a tela ou retornar da tela de cadastro o sistema possa atualizar a lista


        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroying = true;
        NetworkManager.cancelRequests();
    }


    @Override
    public void onResume() {
        // Esse evento é acessado ao abrir o fragment ou retornar do cadastro
        isDestroying = false;
        mProximaPagina = 0;
        mReciclagem.clear();
        carregarWebService(mProximaPagina);
        super.onResume();
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

        Call<RespostaJSON<List<Reciclagem>>> call = NetworkManager.service().listarRecliclagens(pagina);
        call.enqueue(new Callback<RespostaJSON<List<Reciclagem>>>() {

            @Override
            public void onResponse(final Call<RespostaJSON<List<Reciclagem>>> call, final Response<RespostaJSON<List<Reciclagem>>> response) {

                RespostaJSON<List<Reciclagem>> resposta = response.body();
                Log.d("TAG", "onResponse: " + resposta);
                Log.d("TAG", "onResponse: " + resposta.getStatus());
                if (resposta != null && resposta.getStatus() == 0) {
                    atualizarListaReciclagem(resposta.getObject());
                } else {
                    exibirMensagemErro();
                }
            }

            @Override
            public void onFailure(final Call<RespostaJSON<List<Reciclagem>>> call, final Throwable t) {
                Log.e("TAG", "onFailure: ", t);
                exibirMensagemErro();
            }
        });
    }


    private void atualizarListaReciclagem(final List<Reciclagem> reciclagens) {


        // Atualiza os elementos da lista
        if (reciclagens.isEmpty()) {
            mProximaPagina = -1;
        } else {

            mReciclagem.addAll(reciclagens);
            mBinding.reciclagemRecyclerView.getAdapter().notifyDataSetChanged();

            mBinding.reciclagemLoading.setVisibility(View.GONE);
        }
    }

    private void exibirMensagemErro() {
        // Esconde a progressbar
        mBinding.reciclagemLoading.setVisibility(View.GONE);
        if (isDestroying)
            return;
        // Exibe um snackbar com a mensagem de erro
        Snackbar.make(mBinding.getRoot(), R.string.erro_webservice, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onReciclagemClick(final Reciclagem reciclagem) {

        Gson gson = new Gson();
        String json = gson.toJson(reciclagem);
        Intent intent = new Intent(getContext(), CadastraReciclagem.class);
        intent.putExtra("reciclagem", json);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onFotoClick(final Reciclagem reciclagem) {
        // método pré implementado para possível uso futuro
    }


    @Override
    public void onReciclagemEditarClick(Reciclagem reciclagem) {
        // método pré implementado para possível uso futuro
        Gson gson = new Gson();
        String json = gson.toJson(reciclagem);
        Intent intent = new Intent(getContext(), CadastraReciclagem.class);
        intent.putExtra("reciclagem", json);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onReciclagemDeletarClick(final Reciclagem reciclagem) {

        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Deletar Reciclagem")
                .setMessage("Você tem certeza que deseja deletar a reciclagem: " + reciclagem.getTitulo() + "?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<RespostaJSON<Reciclagem>> call = NetworkManager.service().removerRecliclagem(Integer.valueOf(reciclagem.getId()));
                        call.enqueue(new Callback<RespostaJSON<Reciclagem>>() {
                            @Override
                            public void onResponse(Call<RespostaJSON<Reciclagem>> call, Response<RespostaJSON<Reciclagem>> response) {

                                RespostaJSON<Reciclagem> resposta = response.body();
                                Log.d("TAG", "onResponse: " + resposta);
                                Log.d("TAG", "onResponse: " + resposta.getStatus());

                                if (resposta != null && resposta.getStatus() == 0) {
                                    mReciclagem.remove(reciclagem);
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    exibirMensagemErro();
                                }
                            }

                            @Override
                            public void onFailure(Call<RespostaJSON<Reciclagem>> call, Throwable t) {

                                Toast.makeText(getContext(), "Erro deletar!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                })
                .setNegativeButton("Não", null)
                .show();

    }


    void btnListener() {
        mBinding.reciclagemFloatAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CadastraReciclagem.class);
                startActivity(intent);
            }
        });
    }

}


