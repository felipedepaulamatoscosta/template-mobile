package br.utp.sustentabilidade.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import br.utp.sustentabilidade.R;
import br.utp.sustentabilidade.models.Reciclagem;
import br.utp.sustentabilidade.models.RespostaJSON;
import br.utp.sustentabilidade.network.NetworkManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastraReciclagem extends AppCompatActivity {
    private static final String TAG = "Reciclagem";

    private ImageButton btnSalvar;
    private ProgressBar progress_save_loading;
    private EditText titulo;
    private EditText descricao;
    private EditText foto;
    private TextView tipoCadastro;
    private ImageView imgFoto;
    JSONObject json;
    Boolean reciclagemEdit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        try {
            if (getIntent().hasExtra("reciclagem")) {
                json = new JSONObject(getIntent().getStringExtra("reciclagem"));
                reciclagemEdit = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        onInicializar();

        onCarregar();

        onSalvar();

        try {
            Toolbar toolbar = findViewById(R.id.home_toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Reciclagem");

            }

            toolbar.setNavigationOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        } catch (Exception e) {
            Log.i("EERO", e.getMessage());
        }
    }


    private void onCarregar() {
        if (json != null) {

            try {
                titulo.setText(json.getString("titulo"));
                descricao.setText(json.getString("descricao"));
                foto.setText(json.getString("foto"));

                // Exibir foto
                Glide.with(this)
                        .load(json.getString("foto"))
                        .error(R.drawable.ic_placeholder)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(imgFoto);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void onInicializar() {
        titulo = findViewById(R.id.titulo);
        descricao = findViewById(R.id.descricao);
        foto = findViewById(R.id.photo);
        tipoCadastro = findViewById(R.id.tipo_cadastro);
        btnSalvar = findViewById(R.id.salva_obj);
        progress_save_loading = findViewById(R.id.obj_save_loading);
        imgFoto= findViewById(R.id.imgFoto);
        progress_save_loading.setVisibility(View.GONE);
        tipoCadastro.setText("Dados de Reciclagem");

        if(reciclagemEdit)
        {
            btnSalvar.setVisibility(View.GONE);
            descricao.setKeyListener(null);
            titulo.setKeyListener(null);
            foto.setKeyListener(null);
        }
    }

    private void onSalvar() {
        Reciclagem reciclagem = new Reciclagem();
        btnSalvar.setOnClickListener(v -> {

            progress_save_loading.setVisibility(View.VISIBLE);
            btnSalvar.setVisibility(View.GONE);

            if (titulo.toString() != null) {
                reciclagem.setTitulo(titulo.getText().toString());
            }

            if (descricao.toString() != null) {
                reciclagem.setDescricao(descricao.getText().toString());
            }

            if (foto.toString() != null) {
                reciclagem.setFoto(foto.getText().toString());
            }

            Gson gson = new Gson();
            String json = gson.toJson(reciclagem);

            postRequest(reciclagem);
        });
    }




    private void postRequest(Reciclagem reciclagem) {

        Call<RespostaJSON<Reciclagem>> call = NetworkManager.service().inserirRecliclagem(reciclagem);
        call.enqueue(new Callback<RespostaJSON<Reciclagem>>() {
            @Override
            public void onResponse(Call<RespostaJSON<Reciclagem>> call, Response<RespostaJSON<Reciclagem>> response) {


                // Webservice retornou uma resposta
                RespostaJSON<Reciclagem> body = response.body();
                progress_save_loading.setVisibility(View.GONE);
                btnSalvar.setVisibility(View.VISIBLE);

                // Verifica se nao ocorreu erro no processamento do servidor



                if (body == null || body.getStatus() != 0) {
                    Toast.makeText(getBaseContext(), R.string.erro_webservice, Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(getBaseContext(), R.string.sucesso_inclusao, Toast.LENGTH_LONG).show();
                    finish();
                }

            }

            @Override
            public void onFailure(Call<RespostaJSON<Reciclagem>> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                Toast.makeText(getBaseContext(), R.string.erro_webservice, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onBackPressed() { //Botão BACK padrão do android
        startActivity(new Intent(this, MainActivity.class)); //O efeito ao ser pressionado do botão (no caso abre a activity)
        finishAffinity(); //Método para matar a activity e não deixa-lá indexada na pilhagem
        return;
    }
}