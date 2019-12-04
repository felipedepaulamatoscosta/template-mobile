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
import br.utp.sustentabilidade.activities.CadastraResiduo;
import br.utp.sustentabilidade.databinding.FragmentResiduoBinding;
import br.utp.sustentabilidade.models.Residuo;
import br.utp.sustentabilidade.models.RespostaJSON;
import br.utp.sustentabilidade.network.NetworkManager;
import br.utp.sustentabilidade.widgets.adapters.ResiduoAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResiduoFragment extends Fragment implements ResiduoAdapter.ResiduoListener {

    private FragmentResiduoBinding mBinding;
    private List<Residuo> mResiduo;
    private int mProximaPagina = 0;
    private ResiduoAdapter mAdapter;
    private boolean isDestroying;
    /**
     * Construtor de fragmentos.
     *
     * @return Retorna uma instância do fragmento de residuo
     */
    public static ResiduoFragment newInstance() {
        return new ResiduoFragment();
    }

    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_residuo, container, false);

        // Inicializa a lista de residuo
        mResiduo = new ArrayList<>();


        // Inicializa o recycler view
        mAdapter = new ResiduoAdapter(mResiduo, this);
        LinearLayoutManager layout = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        mBinding.residuoRecyclerView.setAdapter(mAdapter);
        mBinding.residuoRecyclerView.setLayoutManager(layout);

        // Exibe a progressbar
        mBinding.residuoLoading.setVisibility(View.VISIBLE);

        // Inclusão do método de verificação da rolagem da lista para carregar a próxima pagina do service
        mBinding.residuoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();

                boolean endHasBeenReached = lastVisible + 5 >= totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached) {
                    if (mProximaPagina >= 0) {
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
        mResiduo.clear();
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

        Call<RespostaJSON<List<Residuo>>> call = NetworkManager.service().listarResiduos(pagina);
        call.enqueue(new Callback<RespostaJSON<List<Residuo>>>() {

            @Override
            public void onResponse(final Call<RespostaJSON<List<Residuo>>> call, final Response<RespostaJSON<List<Residuo>>> response) {

                RespostaJSON<List<Residuo>> resposta = response.body();
                Log.d("TAG", "onResponse: " + resposta);
                Log.d("TAG", "onResponse: " + resposta.getStatus());
                if (resposta != null && resposta.getStatus() == 0) {
                    atualizarListaResiduo(resposta.getObject());
                } else {
                    exibirMensagemErro();
                }
            }

            @Override
            public void onFailure(final Call<RespostaJSON<List<Residuo>>> call, final Throwable t) {
                Log.e("TAG", "onFailure: ", t);
                exibirMensagemErro();
            }
        });
    }


    private void atualizarListaResiduo(final List<Residuo> residuos) {


        // Atualiza os elementos da lista
        if (residuos.isEmpty()) {
            mProximaPagina = -1;
        } else {

            mResiduo.addAll(residuos);
            mBinding.residuoRecyclerView.getAdapter().notifyDataSetChanged();

            mBinding.residuoLoading.setVisibility(View.GONE);
        }
    }

    private void exibirMensagemErro() {
        // Esconde a progressbar
        mBinding.residuoLoading.setVisibility(View.GONE);
        if (isDestroying)
            return;
        // Exibe um snackbar com a mensagem de erro
        Snackbar.make(mBinding.getRoot(), R.string.erro_webservice, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onResiduoClick(final Residuo residuo) {

        Gson gson = new Gson();
        String json = gson.toJson(residuo);
        Intent intent = new Intent(getContext(), CadastraResiduo.class);
        intent.putExtra("residuo", json);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onFotoClick(final Residuo residuo) {
        // método pré implementado para possível uso futuro
        return;
    }


    @Override
    public void onResiduoEditarClick(Residuo residuo) {
        // método pré implementado para possível uso futuro
        Gson gson = new Gson();
        String json = gson.toJson(residuo);
        Intent intent = new Intent(getContext(), CadastraResiduo.class);
        intent.putExtra("residuo", json);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onResiduoDeletarClick(final Residuo residuo) {

        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Deletar Resíduo")
                .setMessage("Você tem certeza que deseja deletar o resíduo: " + residuo.getTitulo() + "?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<RespostaJSON<Residuo>> call = NetworkManager.service().removerResiduo((Integer.valueOf(residuo.getId())));
                        call.enqueue(new Callback<RespostaJSON<Residuo>>() {
                            @Override
                            public void onResponse(Call<RespostaJSON<Residuo>> call, Response<RespostaJSON<Residuo>> response) {

                                RespostaJSON<Residuo> resposta = response.body();
                                Log.d("TAG", "onResponse: " + resposta);
                                Log.d("TAG", "onResponse: " + resposta.getStatus());

                                if (resposta != null && resposta.getStatus() == 0) {
                                    mResiduo.remove(residuo);
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    exibirMensagemErro();
                                }
                            }

                            @Override
                            public void onFailure(Call<RespostaJSON<Residuo>> call, Throwable t) {

                                Toast.makeText(getContext(), "Erro deletar!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                })
                .setNegativeButton("Não", null)
                .show();

    }


    void btnListener() {
        mBinding.residuoFloatAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CadastraResiduo.class);
                startActivity(intent);
            }
        });
    }

}


