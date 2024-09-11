package br.edu.univille.poo.jpa.service;

import br.edu.univille.poo.jpa.entidade.Tarefa;
import br.edu.univille.poo.jpa.repository.TarefaRepository;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.LocalDate;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    public List<Tarefa> obterTodos() {
        return tarefaRepository.findAll();
    }

    public Optional<Tarefa> obterPeloId(Long id) {
        return tarefaRepository.findById(id);
    }

    public List<Tarefa> obterTodosNaoFinalizado() {
        List<Tarefa> todos = new ArrayList<Tarefa>();

        for( var tarefa : tarefaRepository.findAll()) {
            if (!tarefa.isFinalizado()) {
                todos.add(tarefa);
            }
        }

        return todos;
    }

    public List<Tarefa> obterTodosFinalizado() {
        List<Tarefa> todos = new ArrayList<Tarefa>();

        for( var tarefa : tarefaRepository.findAll()) {
            if (tarefa.isFinalizado()) {
                todos.add(tarefa);
            }
        }

        return todos;
    }

    public List<Tarefa> obterTodosAtrasados() {
        List<Tarefa> todos = new ArrayList<Tarefa>();

        // LocalDateTime ldt = LocalDateTime.now();
        LocalDate localDate = LocalDate.now();
        ZoneId z = ZoneId.of( "Brazil/East" );
        Date today = Date.from(localDate.atStartOfDay(z).toInstant());
        // LocalDate today = LocalDate.now( z );  // Always pass a time zone.

        for( var tarefa : tarefaRepository.findAll()) {
            if (tarefa.getDataPrevista().after(today)) {
                todos.add(tarefa);
            }
        }

        return todos;
    }

    public List<Tarefa> obterTodosNaoFinalizadoEntreData(Date inicio, Date fim) {
        List<Tarefa> todos = new ArrayList<Tarefa>();

        // LocalDateTime ldt = LocalDateTime.now();
        // LocalDate today = LocalDate.now( z );  // Always pass a time zone.

        for( var tarefa : tarefaRepository.findAll()) {
            if (tarefa.getDataPrevista().after(inicio) && tarefa.getDataPrevista().before(fim)) {
                todos.add(tarefa);
            }
        }

        return todos;
    }

    public Tarefa incluir(Tarefa tarefa) {
        tarefa.setId(0);
        if(Strings.isBlank(tarefa.getTitulo())){
            throw new RuntimeException("Título não informado.");
        }
        if(tarefa.getTitulo().length() < 5){
            throw new RuntimeException("Título deve conter 5 ou mais caracteres.");
        }
        if(Strings.isBlank(tarefa.getDesc())){
            throw new RuntimeException("Descrição não informado.");
        }
        if(tarefa.getDataPrevista() == null){
            throw new RuntimeException("Data prevista não informada.");
        }

        if(tarefa.isFinalizado()){ // tirar
            throw new RuntimeException("Tarefa não pode estar finalizada.");
        }
        // Adicionar verificação de datas posteriormente

        tarefa = tarefaRepository.save(tarefa);
        return tarefa;
    }

    public Tarefa atualizar(Tarefa tarefa) {
        Tarefa antigo = tarefaRepository.findById(tarefa.getId()).orElse(null);
        
        if(antigo == null){
            throw new RuntimeException("tarefa não foi encontrada.");
        }
        if(tarefa.getTitulo().length() < 5){
            throw new RuntimeException("Título deve conter 5 ou mais caracteres.");
        }
        if (tarefa.isFinalizado()) {
            throw new RuntimeException("Tarefa finalizada não pode ser atualizada.");
        }

        antigo.setTitulo(tarefa.getTitulo());
        antigo.setDesc(tarefa.getDesc());
        // antigo.setFinalizado(tarefa.isFinalizado());
        antigo.setDataPrevista(tarefa.getDataPrevista());

        if(Strings.isBlank(tarefa.getTitulo())){
            throw new RuntimeException("Título não informado.");
        }
        if(Strings.isBlank(tarefa.getDesc())){
            throw new RuntimeException("Descrição não informado.");
        }
        if(tarefa.getDataPrevista() == null){
            throw new RuntimeException("Data prevista não informada.");
        }
        // for(var p : tarefaRepository.findAllByNomeContaining(tarefa.getNome())){
        //     if(antigo.getId() != p.getId()){
        //         throw new RuntimeException("Nome já está cadastrado.");
        //     }
        // }
        
        return tarefaRepository.save(antigo);
    }

    public void excluir(Tarefa tarefa) {
        var antigo = tarefaRepository.findById(tarefa.getId()).orElse(null);
        if(antigo == null){
            throw new RuntimeException("tarefa não encontrada.");
        }
        if (tarefa.isFinalizado()) {
            throw new RuntimeException("Tarefa finalizada não pode ser excluída.");
        }
        tarefaRepository.delete(antigo);
    }

    public Tarefa finalizar(Tarefa tarefa) {
        var antigo = tarefaRepository.findById(tarefa.getId()).orElse(null);
        LocalDate localDate = LocalDate.now();
        ZoneId z = ZoneId.of( "Brazil/East" );
        Date today = Date.from(localDate.atStartOfDay(z).toInstant());

        if(antigo == null){
            throw new RuntimeException("tarefa não encontrada.");
        }
        antigo.setFinalizado(true);
        antigo.setDataFinalizado(today);

        return tarefaRepository.save(antigo);
    }
}
