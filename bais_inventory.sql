-- phpMyAdmin SQL Dump
-- version 4.6.4
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost
-- Tiempo de generación: 21-11-2025 a las 15:04:27
-- Versión del servidor: 5.7.15-log
-- Versión de PHP: 5.6.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `bais_inventory`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ensamble`
--

CREATE TABLE `ensamble` (
  `id_ensamble` int(11) NOT NULL,
  `ubicacion` varchar(10) DEFAULT NULL,
  `id_usuario_responsable` int(11) DEFAULT NULL,
  `nombre` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `ensamble`
--

INSERT INTO `ensamble` (`id_ensamble`, `ubicacion`, `id_usuario_responsable`, `nombre`) VALUES
(34, 'B', 29, 'Manigueta'),
(35, 'B', 29, 'asasd'),
(37, 'B', 29, 'asd');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `exportacion`
--

CREATE TABLE `exportacion` (
  `id_exportacion` int(11) NOT NULL,
  `ubicacion` varchar(10) DEFAULT NULL,
  `destino` varchar(100) DEFAULT NULL,
  `id_usuario_responsable` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `exportacion`
--

INSERT INTO `exportacion` (`id_exportacion`, `ubicacion`, `destino`, `id_usuario_responsable`) VALUES
(15, 'ASD', 'Cali', 29),
(16, 'asd', 'asdasd', 29);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `exportacion_ensamble`
--

CREATE TABLE `exportacion_ensamble` (
  `id_exportacion` int(11) NOT NULL,
  `id_ensamble` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `exportacion_ensamble`
--

INSERT INTO `exportacion_ensamble` (`id_exportacion`, `id_ensamble`) VALUES
(15, 34),
(16, 34),
(16, 35),
(16, 37);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reporte`
--

CREATE TABLE `reporte` (
  `id_reporte` int(11) NOT NULL,
  `id_usuario` int(11) DEFAULT NULL,
  `tipo` varchar(50) DEFAULT NULL,
  `contenido` text,
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP,
  `visto` tinyint(4) DEFAULT '0',
  `fecha_visto` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `reporte`
--

INSERT INTO `reporte` (`id_reporte`, `id_usuario`, `tipo`, `contenido`, `fecha`, `visto`, `fecha_visto`) VALUES
(6, 29, 'Exportaciones', 'asdasdasdasd', '2025-11-19 03:13:05', 0, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `repuesto`
--

CREATE TABLE `repuesto` (
  `id_repuesto` int(11) NOT NULL,
  `ubicacion` varchar(10) DEFAULT NULL,
  `cantidad` int(11) NOT NULL,
  `Nombre` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `repuesto`
--

INSERT INTO `repuesto` (`id_repuesto`, `ubicacion`, `cantidad`, `Nombre`) VALUES
(37, 'C', 7, 'asd'),
(39, 'B', 8, 'Manigueta');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `repuesto_ensamble`
--

CREATE TABLE `repuesto_ensamble` (
  `id_repuesto` int(11) NOT NULL,
  `id_ensamble` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `id_usuario` int(11) NOT NULL,
  `clave_acceso` varchar(20) NOT NULL,
  `contrasena` varchar(255) DEFAULT NULL,
  `rol` enum('trabajador','gerente') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id_usuario`, `clave_acceso`, `contrasena`, `rol`) VALUES
(18, 'usuario', '4c882dcb24bcb1bc225391a602feca7c', 'gerente'),
(29, '1', 'c4ca4238a0b923820dcc509a6f75849b', 'gerente'),
(30, '12', 'c20ad4d76fe97759aa27a0c99bff6710', 'trabajador'),
(31, '13', 'c51ce410c124a10e0db5e4b97fc2af39', 'gerente');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `ensamble`
--
ALTER TABLE `ensamble`
  ADD PRIMARY KEY (`id_ensamble`),
  ADD KEY `id_usuario_responsable` (`id_usuario_responsable`);

--
-- Indices de la tabla `exportacion`
--
ALTER TABLE `exportacion`
  ADD PRIMARY KEY (`id_exportacion`),
  ADD KEY `id_usuario_responsable` (`id_usuario_responsable`);

--
-- Indices de la tabla `exportacion_ensamble`
--
ALTER TABLE `exportacion_ensamble`
  ADD PRIMARY KEY (`id_exportacion`,`id_ensamble`),
  ADD KEY `exportacion_ensamble_ibfk_2` (`id_ensamble`);

--
-- Indices de la tabla `reporte`
--
ALTER TABLE `reporte`
  ADD PRIMARY KEY (`id_reporte`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indices de la tabla `repuesto`
--
ALTER TABLE `repuesto`
  ADD PRIMARY KEY (`id_repuesto`);

--
-- Indices de la tabla `repuesto_ensamble`
--
ALTER TABLE `repuesto_ensamble`
  ADD PRIMARY KEY (`id_repuesto`,`id_ensamble`),
  ADD KEY `id_ensamble` (`id_ensamble`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `clave_acceso` (`clave_acceso`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `ensamble`
--
ALTER TABLE `ensamble`
  MODIFY `id_ensamble` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=38;
--
-- AUTO_INCREMENT de la tabla `exportacion`
--
ALTER TABLE `exportacion`
  MODIFY `id_exportacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;
--
-- AUTO_INCREMENT de la tabla `reporte`
--
ALTER TABLE `reporte`
  MODIFY `id_reporte` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT de la tabla `repuesto`
--
ALTER TABLE `repuesto`
  MODIFY `id_repuesto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;
--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;
--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `ensamble`
--
ALTER TABLE `ensamble`
  ADD CONSTRAINT `ensamble_ibfk_1` FOREIGN KEY (`id_usuario_responsable`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `exportacion`
--
ALTER TABLE `exportacion`
  ADD CONSTRAINT `exportacion_ibfk_1` FOREIGN KEY (`id_usuario_responsable`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `exportacion_ensamble`
--
ALTER TABLE `exportacion_ensamble`
  ADD CONSTRAINT `exportacion_ensamble_ibfk_1` FOREIGN KEY (`id_exportacion`) REFERENCES `exportacion` (`id_exportacion`),
  ADD CONSTRAINT `exportacion_ensamble_ibfk_2` FOREIGN KEY (`id_ensamble`) REFERENCES `ensamble` (`id_ensamble`) ON DELETE CASCADE;

--
-- Filtros para la tabla `reporte`
--
ALTER TABLE `reporte`
  ADD CONSTRAINT `reporte_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `repuesto_ensamble`
--
ALTER TABLE `repuesto_ensamble`
  ADD CONSTRAINT `repuesto_ensamble_ibfk_1` FOREIGN KEY (`id_repuesto`) REFERENCES `repuesto` (`id_repuesto`),
  ADD CONSTRAINT `repuesto_ensamble_ibfk_2` FOREIGN KEY (`id_ensamble`) REFERENCES `ensamble` (`id_ensamble`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
