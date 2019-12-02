package br.utp.sustentabilidade.network;

import java.util.List;

import br.utp.sustentabilidade.models.Agrotoxico;
import br.utp.sustentabilidade.models.Reciclagem;
import br.utp.sustentabilidade.models.ReducaoLixo;
import br.utp.sustentabilidade.models.Residuo;
import br.utp.sustentabilidade.models.RespostaJSON;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SustentabilidadeService {


    @GET("agrotoxico/{id}")
    Call<RespostaJSON<Agrotoxico>> listarAgrotoxico(@Path("id") int id);

    @GET("agrotoxico/all/{pagina}")
    Call<RespostaJSON<List<Agrotoxico>>> listarAgrotoxicos(@Path("pagina") int pagina);

    @POST("agrotoxico/new")
    Call<RespostaJSON<Agrotoxico>> inserirAgrotoxico(@Body Agrotoxico agrotoxico);

    @DELETE("agrotoxico/{id}")
    Call<RespostaJSON<Agrotoxico>> removerAgrotoxico(@Path("id") int id);



    @GET("reciclagem/{id}")
    Call<RespostaJSON<Reciclagem>> listarRecliclagem(@Path("id") int id);

    @GET("reciclagem/all/{pagina}")
    Call<RespostaJSON<List<Reciclagem>>> listarRecliclagens(@Path("pagina") int pagina);

    @POST("reciclagem/new")
    Call<RespostaJSON<Reciclagem>> inserirRecliclagem(@Body Reciclagem reciclagem);

    @DELETE("reciclagem/{id}")
    Call<RespostaJSON<Reciclagem>> removerRecliclagem(@Path("id") int id);



    @GET("reducao_lixo/{id}")
    Call<RespostaJSON<ReducaoLixo>> listarReducaoLixo(@Path("id") int id);

    @GET("reducao_lixo/all/{pagina}")
    Call<RespostaJSON<List<ReducaoLixo>>> listarReducaoLixos(@Path("pagina") int pagina);

    @POST("reducao_lixo/new")
    Call<RespostaJSON<ReducaoLixo>> inserirReducaoLixo(@Body ReducaoLixo reducaoLixo);

    @DELETE("reducao_lixo/{id}")
    Call<RespostaJSON<ReducaoLixo>> removerReducaoLixo(@Path("id") int id);


    @GET("residuo/{id}")
    Call<RespostaJSON<Residuo>> listarResiduo(@Path("id") int id);

    @GET("residuo/all/{pagina}")
    Call<RespostaJSON<List<Residuo>>> listarResiduos(@Path("pagina") int pagina);

    @POST("residuo/new")
    Call<RespostaJSON<Residuo>> inserirResiduo(@Body Residuo residuo);

    @DELETE("residuo/{id}")
    Call<RespostaJSON<Residuo>> removerResiduo(@Path("id") int id);





}
