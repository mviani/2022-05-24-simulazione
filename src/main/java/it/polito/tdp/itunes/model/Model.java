package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;


import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	private ItunesDAO dao;
	private Graph<Track, DefaultWeightedEdge> grafo;
	private Map<Integer, Track> idMap;
	
	private List<Track> percorsoMigliore;
	private int maxSize;
	
	
	public Model() {
		this.dao = new ItunesDAO();
		this.idMap = new HashMap<Integer, Track>();
	}
	
	public String creaGrafo(Genre g) {
		idMap.clear();
		dao.getAllTracks(idMap, g);
		grafo=new SimpleWeightedGraph<Track, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(this.grafo, idMap.values());
		for(Adiacenza ad:dao.getAllEdges(g)) {
			Graphs.addEdge(this.grafo, idMap.get(ad.getId1()),  idMap.get(ad.getId2()), ad.getPeso());
		}
		String s = new String("GRAFO CREATO:\n#Vertici: "+this.grafo.vertexSet().size()+"\nArchi: "+this.grafo.edgeSet().size());
		return s;
		
	}
	
	public List<Genre> getGenres(){
		return dao.getAllGenres();
	}
	
	public List<Adiacenza> getStat(){
		int maxPeso=0;
		List<Adiacenza> res = new ArrayList<Adiacenza>();
		for(DefaultWeightedEdge e:this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)>maxPeso) {
				maxPeso=(int) this.grafo.getEdgeWeight(e);
			}
		}
		
		for(DefaultWeightedEdge e:this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)==maxPeso) {
				res.add( new Adiacenza(this.grafo.getEdgeSource(e).getTrackId(), this.grafo.getEdgeTarget(e).getTrackId(), maxPeso));
			}
		}
		return res;
	}

	public Map<Integer, Track> getIdMap() {
		return idMap;
	}

	public Graph<Track, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}
	
	public List<Track> getPercorso(Track preferita, int limite){
		maxSize=0;
		percorsoMigliore = new ArrayList<Track>();
		List<Track> parziale=new ArrayList<Track>();
		
		
		List<Track> componenteConnessa = new ArrayList<Track>();
		GraphIterator<Track,DefaultWeightedEdge> visita = new BreadthFirstIterator<Track,DefaultWeightedEdge>(this.grafo,preferita);
		while(visita.hasNext()) {
			componenteConnessa.add(visita.next());
		}
		//componenteConnessa.remove(preferita);
		
		
		int peso=preferita.getBytes();
		parziale.add(preferita);
		cercaRicorsiva(parziale,limite,peso,1,componenteConnessa);
		return percorsoMigliore;
	}
	
	public void cercaRicorsiva(List<Track> parziale, int limite,int peso,int livello,List<Track> componenteConnessa) {
		if(peso>limite) {
			return;
		} else {
		if(parziale.size()>maxSize) {
			maxSize=parziale.size();
			percorsoMigliore=new ArrayList<Track>(parziale);
		   }
		}
		if(livello<componenteConnessa.size() && livello>=1) {
		parziale.add(componenteConnessa.get(livello));
		peso=peso+componenteConnessa.get(livello).getBytes();
		cercaRicorsiva(parziale,limite,peso,livello+1,componenteConnessa);
		peso=peso-componenteConnessa.get(livello).getBytes();
		parziale.remove(parziale.size()-1);
		cercaRicorsiva(parziale,limite,peso,livello+1,componenteConnessa);
		//parziale.remove(parziale.size()-1);
		}
		
		
		
	}
	
	
	
	
	
}


/*for(Track t:Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1))) {
	if(!parziale.contains(t)) {
		peso=peso+t.getBytes();
		parziale.add(t);
		cercaRicorsiva(parziale,limite,peso);
		peso=peso-parziale.get(parziale.size()-1).getBytes();
		parziale.remove(parziale.size()-1);
	}
}*/
