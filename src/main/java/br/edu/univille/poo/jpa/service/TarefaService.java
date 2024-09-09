package br.edu.univille.poo.jpa.service;

import br.edu.univille.poo.jpa.entidade.Tarefa;
import br.edu.univille.poo.jpa.repository.TarefaRepository;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Tarefa incluir(Tarefa tarefa) {
        tarefa.setId(0);
        if(Strings.isBlank(tarefa.getTitulo())){
            throw new RuntimeException("Título não informado.");
        }
        if(Strings.isBlank(tarefa.getDesc())){
            throw new RuntimeException("Descrição não informado.");
        }
        if(tarefa.isFinalizado()){
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
        antigo.setNome(tarefa.getNome());
        antigo.setCpf(tarefa.getCpf());
        antigo.setEmail(tarefa.getEmail());
        if(Strings.isBlank(tarefa.getNome())){
            throw new RuntimeException("Nome não informado.");
        }
        if(Strings.isBlank(tarefa.getCpf())){
            throw new RuntimeException("CPF não informado.");
        }
        for(var p : tarefaRepository.findAllByCpf(tarefa.getCpf())){
            if(antigo.getId() != p.getId()){
                throw new RuntimeException("CPF já está cadastrado.");
            }
        }
        for(var p : tarefaRepository.findAllByNomeContaining(tarefa.getNome())){
            if(antigo.getId() != p.getId()){
                throw new RuntimeException("Nome já está cadastrado.");
            }
        }
        return tarefaRepository.save(antigo);
    }

    public void excluir(Tarefa tarefa) {
        var antigo = tarefaRepository.findById(tarefa.getId()).orElse(null);
        if(antigo == null){
            throw new RuntimeException("tarefa não encontrada.");
        }
        tarefaRepository.delete(antigo);
    }
}
