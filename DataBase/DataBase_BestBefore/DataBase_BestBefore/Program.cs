using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using FluentMigrator;
using System.Configuration;

namespace DataBase_BestBefore
{
    class Program
    {
        static void Main(string[] args)
        {
            DatabaseHelper.CreateIfNotExists(ConfigurationManager.ConnectionStrings["connectionStringLocal"].ConnectionString);


        }
    }
}
