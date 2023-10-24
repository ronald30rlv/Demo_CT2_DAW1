package org.cibertec.edu.pe.controller;

import java.util.ArrayList;
import java.util.List;

import org.cibertec.edu.pe.model.Detalle;
import org.cibertec.edu.pe.model.Producto;
import org.cibertec.edu.pe.repository.IDetalleRepository;
import org.cibertec.edu.pe.repository.IProductoRepository;
import org.cibertec.edu.pe.repository.IVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes({"carrito","total"})
public class ProductoController {
	
	// Inicializacion del objeto carrito
	@ModelAttribute("carrito")
	public List<Detalle> getCarrito(){
		return new ArrayList<>();
	}
	
	// Inicializacion del objeto total
	@ModelAttribute("total")
	public double getTotal() {
		return 0.0;
	}
	
	// Declaracion e Inicializacion de objetos para el control del carrito de compras
	@Autowired
	private IProductoRepository productoRepository;
	
	@Autowired
	private IVentaRepository ventaRepository;
	
	@Autowired
	private IDetalleRepository detalleRepository;
	
	// Método para visualizar los productos a vender
	@GetMapping("/index")						// localhost:9090/index
	public String listado(Model model) {
		List<Producto> lista = new ArrayList<>();
		lista = productoRepository.findAll();	// Recuperar las filas de la tabla productos
		model.addAttribute("productos", lista);
		return "index";
	}
	
	// Método para agregar productos al carrito
	@GetMapping("/agregar/{idProducto}")
	public String agregar(Model model,@PathVariable(name="idProducto",required = true) int idProducto) {
		Producto p = productoRepository.findById(idProducto).orElse(null);
		List<Detalle> carrito = (List<Detalle>)model.getAttribute("carrito");
		double total = 0.0;
		boolean existe = false;
		Detalle detalle = new Detalle();
		if(p != null) {
			detalle.setProducto(p);
			detalle.setCantidad(1);
			detalle.setSubtotal(detalle.getProducto().getPrecio() * detalle.getCantidad());
		}
		// Si el carrito esta vacio
		if(carrito.size() == 0) {
			carrito.add(detalle);
		}else {
				for(Detalle d : carrito) {
					if(d.getProducto().getIdProducto() == p.getIdProducto()) {
						d.setCantidad(d.getCantidad() + 1);
						d.setSubtotal(d.getProducto().getPrecio() * d.getCantidad());
						existe = true;
					}
				}
				if(!existe)carrito.add(detalle);
		}
		// Calculando la suma de sub-totales
		for(Detalle d : carrito)total += d.getSubtotal();
		// Guardar valores en la sesion y pasarlos a la vista
		model.addAttribute("total", total);
		model.addAttribute("carrito", carrito);
		return "redirect:/index";
	}
	
	// Método para visualizar el carrito de compras
	@GetMapping("/carrito")
	public String carrito() {
		return "carrito";
	}
}
