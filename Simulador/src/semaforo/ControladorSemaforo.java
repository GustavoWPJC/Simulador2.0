package semaforo;

import Interface.GrafoViewer;
import listener.Listener;
import semaforo.Semaforo;

import java.util.List;

public class ControladorSemaforo implements Listener {
    private List<Semaforo> semaforos;
    private GrafoViewer grafoViewer;


    public ControladorSemaforo(List<Semaforo> semaforos, GrafoViewer grafoViewer) {
        this.semaforos = semaforos;
        this.grafoViewer = grafoViewer;
    }

    @Override
    public void aoDispararEvento(String tipoEvento, Object dados) {
        if ("TICK".equals(tipoEvento)) {
            int tempo = (int) dados;
            for (Semaforo s : semaforos) {
                s.alternarEstado(tempo);
                System.out.println("Semáforo atualizado para: " + s.getEstado());
            }
            grafoViewer.repaint();
        }
    }



}
