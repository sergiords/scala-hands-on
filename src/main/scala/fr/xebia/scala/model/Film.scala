package fr.xebia.scala.model

import fr.xebia.scala.control.CollectionTools

import scala.annotation.tailrec


sealed trait Director

object Director {

  object Kurosawa extends Director {
    override def toString: String = "Kurosawa"
  }

  object Hitchcock extends Director {
    override def toString: String = "Hitchcock"
  }

  object RandomDirector extends Director {
    override def toString: String = "does_not_matter"
  }

}

case class Film(name: String, releaseYear: Int, director: Director, `type`: List[Genre], price: Double = 0)

object Film {

  def getFilmsMadeBy(director: Director, films: List[Film]): List[Film] =
    films.filter(f => f.director == director)

  def filterFilmsWithDirector(films: List[Film])(director: Director): List[Film] =
    films.filter(f => f.director == director)

  def filterFilmsUsingFilter(films: List[Film])(withCustomFilter: Film => Boolean): List[Film] =
    films.filter(withCustomFilter)

  // Note: use Collection#filter
  def filterFilmsUsingFilter2(films: List[Film])(withCustomFilter: Film => Boolean): List[Film] =
    CollectionTools.filter(films, withCustomFilter)

  // Note: use recursion
  def filterFilmsUsingMultipleFilter(films: List[Film])(withCustomFilters: List[Film => Boolean]): List[Film] =
    withCustomFilters match {
      case Nil => films
      case h :: t => filterFilmsUsingMultipleFilter(films.filter(h))(t)
    }

  // Note: use recursion
  def sumPricesWithRecursion(films: List[Film]): Double = {
    @tailrec
    def go(films: List[Film], sum: Double): Double = { films match {
        case Nil => sum
        case h :: tail => go(tail, h.price + sum)
      }
    }
    go(films, 0)
  }

  /**
   * Apply discounts for all films following these rules:
   * - 35% reduction if price is only multiple of 3
   * - 40% reduction if price is only multiple of 5
   * - 50% reduction if price is both multiple of 5 and 3
   * - 0% reduction otherwise
   *
   * Note: use pattern matching
   */
  def discounts(films: List[Film]): List[Double] = films match {
    case Nil => Nil
    case h :: t => (h.price % 3 == 0, h.price % 5 == 0) match {
      case (true, false) => List(h.price * 0.35) ++ discounts(t)
      case (false, true) => List(h.price * 0.4) ++ discounts(t)
      case (true, true) => List(h.price * 0.5) ++ discounts(t)
      case (_, _) => List(h.price) ++ discounts(t)
    }
  }

  // Note: use fold
  def sumPricesWithFolding(films: List[Film]): Double =
    films.foldLeft(0d)((r, c) => r + c.price)

  // Note: use Collection#zip[A,B](List[A], List[B]): (A,B)
  def calculateTotalPrice(films: List[Film], qty: List[Int]): Option[Double] =
    if (films.size == qty.size) {
      Some {
        CollectionTools.zip(films, qty)
          .map { case (film, amt) => film.price * amt}
          .sum
      }
    } else {
      None
    }

  // Note: use Collection#zipWithIndex[A](List[A]): (A,Int)
  def calculateTotalPriceWithIndex(films: List[Film], qty: List[Int]): Option[List[(Int, Double)]] =
    if (films.size == qty.size) {
      Some {
        CollectionTools.zipWithIndex(CollectionTools.zip(films, qty))
          .map { case ((f: Film, qty: Int), index) => (index, f.price * qty)}
      }
    } else {
      None
    }
}