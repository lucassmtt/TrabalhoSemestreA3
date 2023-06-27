package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.AulaDao;
import model.entities.Aula;
import model.entities.Sala;
import model.entities.Turma;
import model.tools.Exibir;

import javax.print.attribute.standard.JobKOctets;
import java.sql.*;

public class AulaDaoJDBC implements AulaDao
{
    private Connection connection = null;

    public AulaDaoJDBC(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void inserirAula(Aula aula)
    {
        if (connection != null){
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try{
                String sql = "INSERT INTO faculdade.aula (nome_aula, ID_sala, ID_turma, dia_semana) " +
                        "VALUES (?, ?, ?, ?);";
                preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                preparedStatement.setObject(1, aula.se_existir_nome_retorna_nome_ou_null());
                preparedStatement.setObject(2, aula.se_existir_a_sala_retorna_id_ou_null());
                preparedStatement.setObject(3, aula.se_existir_a_turma_retorna_id_ou_null());
                preparedStatement.setObject(4, aula.se_existir_a_dia_semana_retorna_dia_ou_null());

                int linhas_afetadas = preparedStatement.executeUpdate();

                if (linhas_afetadas > 0) {
                    resultSet = preparedStatement.getGeneratedKeys();
                    if (resultSet.next()){
                        int ID = resultSet.getInt(1);
                        aula.setIdAula(ID);
                    }
                    DB.fechaResultSet(resultSet);
                    System.out.println("Inserção da aula no banco de dados feita com sucesso...");
                }
                else {
                    System.out.println("Impossível inserir aula! ");
                }
            }
            catch (Exception e){
                throw new DbException(e.getMessage());
            }
            finally {
                DB.fechaStatement(preparedStatement);
            }
        }
        else {
            System.out.println("Impossível inserir dados com a conexão nula...");
        }

    }

    @Override
    public void apagarAulaPorId(Integer ID)
    {
        if (connection != null)
        {
            PreparedStatement preparedStatement = null;

            try {
                String sql = "DELETE FROM faculdade.aula WHERE ID_aula = ?;";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, ID);

                int rows_affect = preparedStatement.executeUpdate();

                if (rows_affect > 0) {
                    System.out.println("Aula apagada com sucesso...");
                }
                else {
                    System.out.println("Deleção incompleta...");
                }
            }
            catch (SQLException e)
            {
                throw new DbException(e.getMessage());
            }
            finally {
                DB.fechaStatement(preparedStatement);
            }
        }
        else {
            System.out.println("Impossível apagar dados com a conexão nula...");
        }

    }

    @Override
    public void atualizarAula(Aula aula)
    {
        if (connection != null)
        {
            PreparedStatement preparedStatement = null;
            try{
                String sql = "UPDATE faculdade.aula " +
                        "SET nome_aula = ?, ID_sala = ?, ID_turma = ?, dia_semana = ?" +
                        "WHERE ID_aula = ?;";
                preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                preparedStatement.setObject(1, aula.se_existir_nome_retorna_nome_ou_null());
                preparedStatement.setObject(2, aula.se_existir_a_sala_retorna_id_ou_null());
                preparedStatement.setObject(3, aula.se_existir_a_turma_retorna_id_ou_null());
                preparedStatement.setObject(4, aula.se_existir_a_dia_semana_retorna_dia_ou_null());
                preparedStatement.setInt(5, aula.getIdAula());

                int linhas_afetadas = preparedStatement.executeUpdate();

                if (linhas_afetadas > 0){
                    System.out.println("Aula atualizada com sucesso...");
                }
                else {
                    System.out.println("Impossível atualizar aula...");
                }

            } catch (SQLException e) {
                throw new DbException(e.getMessage());
            }
            finally {
                DB.fechaStatement(preparedStatement);
            }
        }
        else {
            System.out.println("Impossível atualizar dados com a conexão nula...");
        }

    }

    @Override
    public void buscarAulaPorId(Integer ID)
    {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        if (connection != null)
        {
            try {
                preparedStatement = connection.prepareStatement(
                        "SELECT * FROM faculdade.aula where ID_aula = ?;"
                );
                preparedStatement.setInt(1, ID);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    System.out.println("_____________________________");
                    System.out.println("ID Aula: " + resultSet.getInt(1));
                    Object nome_aula = resultSet.getObject(2);
                    Object ID_sala = resultSet.getObject(3);
                    Object ID_turma = resultSet.getObject(4);
                    Object dia_semana = resultSet.getObject(5);

                    if (nome_aula == null) {
                        System.out.println("Não existe nenhum nome para esta sala...");
                    } else {
                        System.out.println("Nome aula: " + nome_aula);
                    }

                    if (ID_sala == null) {
                        System.out.println("Não existe nenhuma sala anexada a aula...");
                    } else {
                        System.out.println("ID sala: " + ID_sala);
                    }

                    if (ID_turma == null) {
                        System.out.println("Não existe nenhuma turma anexada a sala...");
                    }
                    else {
                        System.out.println("ID turma: " + ID_turma);
                    }

                    if (dia_semana == null) {
                        System.out.println("A aula não está anexada a nenhum dia da semana...");
                    } else {
                        System.out.println("Dias de aula: " + dia_semana);
                    }
                }
                else {
                    System.out.println("Nenhum registro encontrado...");
                }

            }
            catch (SQLException e){
                throw new DbException(e.getMessage());
            }
            finally {
                DB.fechaStatement(preparedStatement);
            }
        }
        else {
            System.out.println("Impossível buscar dado com a conexão nula...");
        }

    }

    @Override
    public Aula buscarAulaPorIdTransformarEmObjAula(Integer ID)
    {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        if (connection != null)
        {
            try {
                preparedStatement = connection.prepareStatement(
                        "SELECT * FROM faculdade.aula where ID_aula = ?;"
                );
                preparedStatement.setInt(1, ID);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()){
                    Aula aula = new Aula();
                    Sala sala = new Sala();
                    Turma turma = new Turma();

                    aula.setNomeAula(resultSet.getString(5));
                    aula.setDiaSemana(resultSet.getString(4));

                    Object obj = resultSet.getObject(2);
                    if (obj == null){
                        sala.setId_Sala(null);
                    }
                    else {
                        sala.setId_Sala(resultSet.getInt(2));
                    }

                    obj = resultSet.getObject(3);
                    if (obj == null){
                        turma.setId_Turma(null);
                    }
                    else {
                        turma.setId_Turma(resultSet.getInt(3));
                    }
                    aula.setSala(sala);
                    aula.setTurma(turma);
                    return aula;
                }
                else {
                    System.out.println("Nenhum registro encontrado...");
                }
                DB.fechaResultSet(resultSet);

            }
            catch (SQLException e){
                throw new DbException(e.getMessage());
            }
            finally {
                DB.fechaStatement(preparedStatement);
            }
        }
        else {
            System.out.println("Impossível buscar dado com a conexão nula...");
        }
        return null;
    }

    @Override
    public void buscarTodosAulas()
    {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        if (connection != null)
        {
            try {
                preparedStatement = connection.prepareStatement(
                        "SELECT * FROM faculdade.aula;"
                );
                resultSet = preparedStatement.executeQuery();
                Object ID_sala, ID_turma, dia_semana, nome_aula;
                while (resultSet.next()){

                    System.out.println("______________________________________");
                    System.out.println("ID Aula: " + resultSet.getInt(1));
                    nome_aula = resultSet.getObject(2);
                    ID_sala = resultSet.getObject(3);
                    ID_turma = resultSet.getObject(4);
                    dia_semana = resultSet.getObject(5);

                    if (nome_aula == null) {
                        System.out.println("Não existe nenhum nome para esta sala...");
                    } else {
                        System.out.println("Nome aula: " + nome_aula);
                    }

                    if (ID_sala == null) {
                        System.out.println("Não existe nenhuma sala anexada a aula...");
                    } else {
                        System.out.println("ID sala: " + ID_sala);
                    }

                    if (ID_turma == null) {
                        System.out.println("Não existe nenhuma turma anexada a sala...");
                    }
                    else {
                        System.out.println("ID turma: " + ID_turma);
                    }

                    if (dia_semana == null) {
                        System.out.println("A aula não está anexada a nenhum dia da semana...");
                    } else {
                        System.out.println("Dias de aula: " + dia_semana);
                    }
                    Exibir.espera_em_ms(500);
                }
                DB.fechaResultSet(resultSet);
            }
            catch (SQLException e){
                throw new DbException(e.getMessage());
            }
            finally {
                DB.fechaStatement(preparedStatement);
            }
        }
        else {
            System.out.println("Impossível buscar dados com a conexão nula...");
        }

    }
}
