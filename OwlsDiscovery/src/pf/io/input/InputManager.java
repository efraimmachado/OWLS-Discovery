package pf.io.input;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owls.expression.Condition;
import org.mindswap.owls.process.Result;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.process.variable.Parameter;
import org.mindswap.swrl.Atom;
import org.mindswap.swrl.AtomVisitor;
import org.mindswap.swrl.BuiltinAtom;
import org.mindswap.swrl.ClassAtom;
import org.mindswap.swrl.DataPropertyAtom;
import org.mindswap.swrl.DifferentIndividualsAtom;
import org.mindswap.swrl.IndividualPropertyAtom;
import org.mindswap.swrl.SameIndividualAtom;

import pf.resource.MessageProperties;
import pf.vo.Query;
import pf.vo.Service;

/**
 * @author Rodrigo Almeida de Amorim
 * @version 0.1
 */

/**
 * Class that takes care of data entry.
 */
public class InputManager {

	/**
	 * Attribute that holds all services.
	 */
	private ArrayList<Service> services = null;

	/**
	 * Attribute that holds the query.
	 */
	private Query request = null;

	/**
	 * Method that converts an InputList/OutputList to an ArrayList of URI. 
	 * @param parameters - An InputList/OutputList.
	 * @return uriList - An ArrayList of URI.
	 */
	private ArrayList<URI> getURIListInput(OWLIndividualList<Input> parameters) {
		Iterator<Input> iter = parameters.iterator();
		ArrayList<URI> uriList = new ArrayList<URI>();

		while (iter.hasNext()) {
			uriList.add(((Parameter) iter.next()).getParamType().getURI());
		}
		return uriList;
	}
	
	private ArrayList<URI> getURIListOutput(OWLIndividualList<Output> parameters) {
		Iterator<Output> iter = parameters.iterator();
		ArrayList<URI> uriList = new ArrayList<URI>();

		while (iter.hasNext()) {
			uriList.add(((Parameter) iter.next()).getParamType().getURI());
		}
		return uriList;
	}


	/**
	 * Method that stores the Data Entry.
	 * 
	 * @param arguments -
	 *            An array with the arguments of the program. - Path to the
	 *            request and response
	 * @return - True if it stores the data correctly and False if the store
	 *         fail.
	 * @throws IOException 
	 */
	@SuppressWarnings("static-access")
	public boolean readDataEntry(String[] arguments) throws IOException {

		// TODO: Tratamento caso ao invés do usuário informar o endereco, informe um URI. -- Rodrigo
		File fileRequest = new File(arguments[0]);
		File directoryService = new File(arguments[1]);
		File[] fileServices = null;
		Properties property = MessageProperties.getInstance();
//		OWLKnowledgeBase base = OWLFactory.createKB();
		URI uri = null;
//		OWLOntology onto = null;
		
//		Service KB
		final OWLKnowledgeBase kbS = OWLFactory.createKB();
//		Query KB		
		final OWLKnowledgeBase kbQ = OWLFactory.createKB();
		
		
		services = new ArrayList<Service>();

		if (arguments.length < 2) {
			return false;
		} else {
			try {
				if (directoryService.isDirectory()) {
					fileServices = directoryService.listFiles();

					/*
					 * Reading the services.
					 */
					
					for (int i = 0; i < fileServices.length; i++) {
						if (fileServices[i].isFile()) {
							uri = fileServices[i].toURI();
							
							System.out.println(uri);
							
							final org.mindswap.owls.service.Service service = kbS.readService(uri);
							
							final OWLIndividualList<Condition> cs = service.getProfile().getConditions();
							final ArrayList<ArrayList<URI>> conditions = new ArrayList<ArrayList<URI>>();
							
														
							for (final Condition<?> c : cs)
							{
//								System.out.println(c.getURI());
								
								if (c.canCastTo(Condition.SWRL.class)) // is it a SWRL condition?
								{
									final Condition.SWRL sc = c.castTo(Condition.SWRL.class);
									for (final Atom a : sc.getBody())
									{
										a.accept(new AtomVisitor() {

											public void visit(final IndividualPropertyAtom atom)
											{
												URI aux = null;
												final ArrayList<URI> uris = new ArrayList<URI>();
												
												URI a1 = aux.create((atom.getArgument1().getNamespace().toString() + atom.getArgument1().toString()));
												URI a2 = aux.create((atom.getArgument2().getNamespace().toString() + atom.getArgument2().toString()));
												URI p = aux.create(atom.getPropertyPredicate().toString());
												
//												System.out.println("S ARGUMENTO 1 " + a1);
//												System.out.println("S ARGUMENTO 2 " + a2);
//												System.out.println("S PREDICATE " + p);
												
												uris.add(p);
												uris.add(a1);
												uris.add(a2);
												
												conditions.add(uris);
												
											}
											public void visit(final DataPropertyAtom atom) { }
											public void visit(final SameIndividualAtom atom) { }
											public void visit(final DifferentIndividualsAtom atom) {	}
											public void visit(final ClassAtom atom) { }
											public void visit(final BuiltinAtom atom)	{ }
										});
									}
								}
							}		
							
							final OWLIndividualList<Result> ef = service.getProfile().getResults();
							final ArrayList<ArrayList<URI>> effects = new ArrayList<ArrayList<URI>>();
							
							for (final Result c : ef)
							{
//								System.out.println(c.getURI());
								if (c.canCastTo(Condition.SWRL.class)) // is it a SWRL condition?
								{
									final Condition.SWRL sc = c.castTo(Condition.SWRL.class);
									for (final Atom a : sc.getBody())
									{
										a.accept(new AtomVisitor() {

											public void visit(final IndividualPropertyAtom atom)
											{
												URI aux = null;
												final ArrayList<URI> uris = new ArrayList<URI>();
												
												URI a1 = aux.create((atom.getArgument1().getNamespace().toString() + atom.getArgument1().toString()));
												URI a2 = aux.create((atom.getArgument2().getNamespace().toString() + atom.getArgument2().toString()));
												URI p = aux.create(atom.getPropertyPredicate().toString());
												
												System.out.println("S ARGUMENTO 1 " + a1);
												System.out.println("S ARGUMENTO 2 " + a2);
												System.out.println("S PREDICATE " + p);
												
												uris.add(p);
												uris.add(a1);
												uris.add(a2);												
												
												effects.add(uris);
												
											}
											public void visit(final DataPropertyAtom atom) { }
											public void visit(final SameIndividualAtom atom) { }
											public void visit(final DifferentIndividualsAtom atom) {	}
											public void visit(final ClassAtom atom) { }
											public void visit(final BuiltinAtom atom)	{ }
										});
									}
								}
							}



						System.out.println("S INPUTS " + getURIListInput(service.getProfile().getInputs()));
						System.out.println("S OUTPUTS " + getURIListOutput(service.getProfile().getOutputs()));
						
							
							services.add(new Service(getURIListInput(service.getProfile().getInputs()), 
									getURIListOutput(service.getProfile().getOutputs()), 
											conditions, effects, uri));

							
							kbS.unload(uri);
						}
					}
					
					/*
					 * Reading the request.
					 */
					if (fileRequest.isFile()) {
						uri = fileRequest.toURI();
						
						System.out.println(uri);
						
						final org.mindswap.owls.service.Service query = kbQ.readService(uri);
						
						final OWLIndividualList<Condition> cs = query.getProfile().getConditions();
						final ArrayList<ArrayList<URI>> conditions = new ArrayList<ArrayList<URI>>();
						
													
						for (final Condition<?> c : cs)
						{
//							System.out.println(c.getURI());
							if (c.canCastTo(Condition.SWRL.class)) // is it a SWRL condition?
							{
								final Condition.SWRL sc = c.castTo(Condition.SWRL.class);
								for (final Atom a : sc.getBody())
								{
									a.accept(new AtomVisitor() {

										public void visit(final IndividualPropertyAtom atom)
										{
											URI aux = null;
											final ArrayList<URI> uris = new ArrayList<URI>();
											
											URI a1 = aux.create((atom.getArgument1().getNamespace().toString() + atom.getArgument1().toString()));
											URI a2 = aux.create((atom.getArgument2().getNamespace().toString() + atom.getArgument2().toString()));
											URI p = aux.create(atom.getPropertyPredicate().toString());
											
											System.out.println("R ARGUMENTO 1 " + a1);
											System.out.println("R ARGUMENTO 2 " + a2);
											System.out.println("R PREDICATE " + p);
											
											uris.add(p);
											uris.add(a1);
											uris.add(a2);
											
											conditions.add(uris);
											
										}
										public void visit(final DataPropertyAtom atom) { }
										public void visit(final SameIndividualAtom atom) { }
										public void visit(final DifferentIndividualsAtom atom) {	}
										public void visit(final ClassAtom atom) { }
										public void visit(final BuiltinAtom atom)	{ }
									});
								}
							}
						}		
						
						final OWLIndividualList<Result> ef = query.getProfile().getResults();
						final ArrayList<ArrayList<URI>> effects = new ArrayList<ArrayList<URI>>();
						
						for (final Result c : ef)
						{
//							System.out.println(c.getURI());
							if (c.canCastTo(Condition.SWRL.class)) // is it a SWRL condition?
							{
								final Condition.SWRL sc = c.castTo(Condition.SWRL.class);
								for (final Atom a : sc.getBody())
								{
									a.accept(new AtomVisitor() {

										public void visit(final IndividualPropertyAtom atom)
										{
											URI aux = null;
											final ArrayList<URI> uris = new ArrayList<URI>();
											
											URI a1 = aux.create((atom.getArgument1().getNamespace().toString() + atom.getArgument1().toString()));
											URI a2 = aux.create((atom.getArgument2().getNamespace().toString() + atom.getArgument2().toString()));
											URI p = aux.create(atom.getPropertyPredicate().toString());
											
											System.out.println("R ARGUMENTO 1 " + a1);
											System.out.println("R ARGUMENTO 2 " + a2);
											System.out.println("R PREDICATE " + p);
											
											uris.add(p);
											uris.add(a1);
											uris.add(a2);
											
											effects.add(uris);
											
										}
										public void visit(final DataPropertyAtom atom) { }
										public void visit(final SameIndividualAtom atom) { }
										public void visit(final DifferentIndividualsAtom atom) {	}
										public void visit(final ClassAtom atom) { }
										public void visit(final BuiltinAtom atom)	{ }
									});
								}
							}
						}

						
//FIXME Ta pegando errado o input					
					
					System.out.println("R INPUTS " + getURIListInput(query.getProfile().getInputs()));
					System.out.println("R OUTPUTS " + getURIListOutput(query.getProfile().getOutputs()));
					
						
						request = new Query(getURIListInput(query.getProfile().getInputs()), 
								getURIListOutput(query.getProfile().getOutputs()), 
										conditions, effects, uri);

						
						kbQ.unload(uri);
			
					}					
					return true;
				} else {
					return false;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * @return the services.
	 */
	public ArrayList<Service> getServices() {
		return services;
	}

	/**
	 * @param services
	 *            the services to set.
	 */
	public void setServices(ArrayList<Service> services) {
		this.services = services;
	}

	/**
	 * @return the request.
	 */
	public Query getRequest() {
            return request;
	}

	/**
	 * @param request
	 *            the request to set.
	 */
	public void setRequest(Query request) {
            this.request = request;
	}

        public void addService (Service service)
	{
            this.services.add(service);
	}
}
