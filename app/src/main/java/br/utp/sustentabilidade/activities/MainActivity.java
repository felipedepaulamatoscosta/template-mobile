package br.utp.sustentabilidade.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import br.utp.sustentabilidade.R;
import br.utp.sustentabilidade.databinding.ActivityMainBinding;
import br.utp.sustentabilidade.fragments.AgrotoxicoFragment;
import br.utp.sustentabilidade.fragments.ReciclagemFragment;
import br.utp.sustentabilidade.fragments.ReducaoLixoFragment;
import br.utp.sustentabilidade.fragments.ResiduoFragment;
import br.utp.sustentabilidade.models.Reciclagem;
import br.utp.sustentabilidade.models.ReducaoLixo;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    private AgrotoxicoFragment mFragmentoAgrotoxico;
    private ReciclagemFragment mFragmentoReciclagem;
    private ReducaoLixoFragment mFragmentoReducaoLixo;
    private ResiduoFragment mFragmentoResiduo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Inicializa os fragmentos
        mFragmentoAgrotoxico = AgrotoxicoFragment.newInstance();
        mFragmentoReciclagem = ReciclagemFragment.newInstance();
        mFragmentoReducaoLixo = ReducaoLixoFragment.newInstance();
        mFragmentoResiduo= ResiduoFragment.newInstance();

        // Cadastra os eventos da bottom navigation
        mBinding.mainBottomNavigation.setOnNavigationItemSelectedListener(this::onSelecionarFragmento);

        // Define o primeiro fragmento a ser visualizado
        trocarFragmentos(mFragmentoReciclagem);
    }

    private boolean onSelecionarFragmento(final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_reciclagem:
                trocarFragmentos(mFragmentoReciclagem);
                return true;
            case R.id.action_agrotoxicos:
                trocarFragmentos(mFragmentoAgrotoxico);
                return true;
            case R.id.action_reducao_lixo:
                trocarFragmentos(mFragmentoReducaoLixo);
                return true;

            case R.id.action_residuos:
                trocarFragmentos(mFragmentoResiduo);
                return true;
        }
        return false;
    }

    private void trocarFragmentos(final Fragment fragmento) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.main_content, fragmento);
        transaction.commit();
    }
}
