package br.utp.sustentabilidade.activities;


import android.content.Context;
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


import org.json.JSONException;
import org.json.JSONObject;

import br.utp.sustentabilidade.R;
import br.utp.sustentabilidade.models.Agrotoxico;
import br.utp.sustentabilidade.models.RespostaJSON;
import br.utp.sustentabilidade.network.NetworkManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastraAgrotoxico extends AppCompatActivity {
    private static final String TAG = "Agrotoxico";

    private ImageButton btnSalvar;
    private ProgressBar progress_save_loading;
    private EditText titulo;
    private EditText descricao;
    private EditText foto;
    private TextView tipoCadastro;
    private ImageView imgFoto;
    JSONObject json;
    Boolean agrotoxicoEdit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // uso um xml genérico nesse caso pois todos os cadastros são iguais
        setContentView(R.layout.activity_cadastro);

        try {
            if (getIntent().hasExtra("agrotoxico")) {

                json = new JSONObject(getIntent().getStringExtra("agrotoxico"));
                //caso haja um "Extra" e seja possível converter significa que essa tela foi criada a aprtir do evento Click do card ou onEdit do card

                agrotoxicoEdit = true;
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
                getSupportActionBar().setTitle("Agrotóxico");

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

        // troco o texto de título pois o xml activity_cadastro é genérico
        tipoCadastro.setText("Dados do Agrotóxico");


        //caso não seja um item novo escondemos o botão salvar e bloqueamos a entrada de digitação nos campos
        //isso porque o método de edição não está implementado, então usamos essa tela como detalhe
        if(agrotoxicoEdit)
        {
            // View.GONE oculta o componente e libera o espaço do layout
            btnSalvar.setVisibility(View.GONE);
            // setKeyListener(null) bloqueia a entrada de digitação no campo
            descricao.setKeyListener(null);
            titulo.setKeyListener(null);
            foto.setKeyListener(null);
        }

        //se a variavel agrotoxicoEdit = false sabemos que estamos inserindo uum item novo
    }

    private void onSalvar() {
        Agrotoxico agrotoxico = new Agrotoxico();




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
                agrotoxico.setTitulo(titulo.getText().toString());
            }

            if (descricao.toString() != null) {
                agrotoxico.setDescricao(descricao.getText().toString());
            }

            if (foto.toString() != null) {
                agrotoxico.setFoto(foto.getText().toString());
            }


            // nesse procedimento oculta o botão salvar e torna o progress visivel para até finalizar a operação no serviço
            progress_save_loading.setVisibility(View.VISIBLE);
            btnSalvar.setVisibility(View.GONE);

            postRequest(agrotoxico);
        });
    }


    private void postRequest(Agrotoxico agrotoxico) {

        // nesse momento pode se incluir o método editar se necessário...
        // apenas verificar a variavel agrotoxicoEdit para trocar a chamado do método
        Call<RespostaJSON<Agrotoxico>> call = NetworkManager.service().inserirAgrotoxico(agrotoxico);
        call.enqueue(new Callback<RespostaJSON<Agrotoxico>>() {
            @Override
            public void onResponse(Call<RespostaJSON<Agrotoxico>> call, Response<RespostaJSON<Agrotoxico>> response) {


                // Webservice retornou uma resposta
                RespostaJSON<Agrotoxico> body = response.body();

                // torno o progress invisivel pois o processamento no service terminou
                progress_save_loading.setVisibility(View.GONE);

                // torno o botão salvar visivel novamente
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
            public void onFailure(Call<RespostaJSON<Agrotoxico>> call, Throwable t) {
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