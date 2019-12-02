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
import br.utp.sustentabilidade.models.ReducaoLixo;
import br.utp.sustentabilidade.models.RespostaJSON;
import br.utp.sustentabilidade.network.NetworkManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastraReducao extends AppCompatActivity {
    private static final String TAG = "Reducao";

    private ImageButton btnSalvar;
    private ProgressBar progress_save_loading;
    private EditText titulo;
    private EditText descricao;
    private EditText foto;
    private TextView tipoCadastro;
    private ImageView imgFoto;
    JSONObject json;
    Boolean reducaoEdit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        try {
            if (getIntent().hasExtra("reducaoLixo")) {
                json = new JSONObject(getIntent().getStringExtra("reducaoLixo"));
                reducaoEdit = true;
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
                getSupportActionBar().setTitle("Redução Lixo");

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
        tipoCadastro.setText("Dados de Redução de Lixo");

        if(reducaoEdit)
        {
            btnSalvar.setVisibility(View.GONE);
            descricao.setKeyListener(null);
            titulo.setKeyListener(null);
            foto.setKeyListener(null);
        }
    }

    private void onSalvar() {
        ReducaoLixo reducaoLixo = new ReducaoLixo();
        btnSalvar.setOnClickListener(v -> {

            if(titulo.getText().toString().equals(""))
            {
                titulo.requestFocus();
                Toast.makeText(this, R.string.erro_informetitulo, Toast.LENGTH_LONG).show();
                return;
            }

            if(descricao.getText().toString().equals(""))
            {
                descricao.requestFocus();
                Toast.makeText(this, R.string.erro_informedescricao, Toast.LENGTH_LONG).show();
                return;
            }


            if (titulo.toString() != null) {
                reducaoLixo.setTitulo(titulo.getText().toString());
            }

            if (descricao.toString() != null) {
                reducaoLixo.setDescricao(descricao.getText().toString());
            }

            if (foto.toString() != null) {
                reducaoLixo.setFoto(foto.getText().toString());
            }

            Gson gson = new Gson();
            String json = gson.toJson(reducaoLixo);
            progress_save_loading.setVisibility(View.VISIBLE);
            btnSalvar.setVisibility(View.GONE);

            postRequest(reducaoLixo);
        });
    }




    private void postRequest(ReducaoLixo reducaoLixo) {

        Call<RespostaJSON<ReducaoLixo>> call = NetworkManager.service().inserirReducaoLixo(reducaoLixo);
        call.enqueue(new Callback<RespostaJSON<ReducaoLixo>>() {
            @Override
            public void onResponse(Call<RespostaJSON<ReducaoLixo>> call, Response<RespostaJSON<ReducaoLixo>> response) {


                // Webservice retornou uma resposta
                RespostaJSON<ReducaoLixo> body = response.body();
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
            public void onFailure(Call<RespostaJSON<ReducaoLixo>> call, Throwable t) {
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